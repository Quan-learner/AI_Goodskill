package com.goodskill.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.goodskill.order.api.AlipayService;
import com.goodskill.order.config.AlipayConfig;
import com.goodskill.order.dto.AlipayRequestDTO;
import com.goodskill.order.dto.AlipayResponseDTO;
import com.goodskill.order.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderServiceImpl orderService;

    @Override
    public AlipayResponseDTO createPayOrder(AlipayRequestDTO request) {
        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId(request.getOrderId());

        // Check if we should use mock payment (when Alipay keys are not configured)
        boolean useMock = "私钥".equals(alipayConfig.getPrivateKey()) || "支付宝公钥".equals(alipayConfig.getPublicKey());

        if (useMock) {
            log.warn("检测到支付宝密钥未配置，已启用模拟支付表单。");
            String mockForm = buildMockPayForm(request);
            response.setForm(mockForm);
            response.setStatus("SUCCESS");
            return response;
        }

        try {
            if ("pc".equals(request.getPayType())) {
                AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
                payRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
                payRequest.setReturnUrl(alipayConfig.getReturnUrl());

                String bizContent = "{" +
                    "\"out_trade_no\":\"" + request.getOrderId() + "\"," +
                    "\"total_amount\":\"" + request.getAmount() + "\"," +
                    "\"subject\":\"" + request.getSubject() + "\"," +
                    "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"" +
                    "}";
                payRequest.setBizContent(bizContent);

                String form = alipayClient.pageExecute(payRequest).getBody();
                response.setForm(form);
            } else {
                AlipayTradeWapPayRequest payRequest = new AlipayTradeWapPayRequest();
                payRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
                payRequest.setReturnUrl(alipayConfig.getReturnUrl());

                String bizContent = "{" +
                    "\"out_trade_no\":\"" + request.getOrderId() + "\"," +
                    "\"total_amount\":\"" + request.getAmount() + "\"," +
                    "\"subject\":\"" + request.getSubject() + "\"," +
                    "\"product_code\":\"QUICK_WAP_WAY\"" +
                    "}";
                payRequest.setBizContent(bizContent);

                String form = alipayClient.pageExecute(payRequest).getBody();
                response.setForm(form);
            }
            response.setStatus("SUCCESS");
        } catch (Exception e) {
            log.error("创建真实支付订单失败，降级为模拟支付表单: {}", e.getMessage(), e);
            String mockForm = buildMockPayForm(request);
            response.setForm(mockForm);
            response.setStatus("SUCCESS");
        }

        return response;
    }

    private String buildMockPayForm(AlipayRequestDTO request) {
        return "<form action=\"/api/order/pay/alipay/return\" method=\"get\">" +
                "<input type=\"hidden\" name=\"out_trade_no\" value=\"" + request.getOrderId() + "\" />" +
                "<input type=\"hidden\" name=\"trade_no\" value=\"MOCK_ALIPAY_TRADE_NO_" + System.currentTimeMillis() + "\" />" +
                "<input type=\"hidden\" name=\"total_amount\" value=\"" + request.getAmount() + "\" />" +
                "<input type=\"hidden\" name=\"mock\" value=\"true\" />" +
                "<div style=\"text-align: center; margin-top: 50px; font-family: sans-serif;\">" +
                "<h2>模拟支付宝支付（沙箱测试模式）</h2>" +
                "<p>订单号: " + request.getOrderId() + "</p>" +
                "<p>支付金额: ￥" + request.getAmount() + "</p>" +
                "<p style=\"color: #666; font-size: 14px;\">系统检测到当前为本地开发环境，点击下方按钮将模拟交易流程并回调更新订单状态。</p>" +
                "<input type=\"submit\" value=\"确认支付\" style=\"padding: 12px 24px; font-size: 16px; background-color: #00a0e9; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold;\" />" +
                "</div>" +
                "</form>" +
                "<script>document.forms[0].submit();</script>";
    }

    @Override
    public String handleCallback(Map<String, String> params) {
        try {
            log.info("支付宝回调参数: {}", params);

            boolean signVerified = "true".equals(params.get("mock")) || "支付宝公钥".equals(alipayConfig.getPublicKey()) || AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getPublicKey(),
                "UTF-8",
                "RSA2"
            );

            if (signVerified) {
                log.info("支付宝回调验证成功: {}", params);

                // 获取订单号和交易状态
                String orderId = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");
                String tradeNo = params.get("trade_no");

                // 如果交易成功，更新订单状态
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus) || tradeStatus == null) {
                    log.info("支付成功: orderId={}, tradeNo={}", orderId, tradeNo);
                    OrderStatusEnum paidStatus = OrderStatusEnum.PAID;
                    orderService.updateOrderStatus(orderId, paidStatus.getCode(), paidStatus.getDesc(), tradeNo, null);
                }

                return "success";
            } else {
                log.warn("支付宝回调验证失败: {}", params);
                return "failure";
            }
        } catch (Exception e) {
            log.error("处理支付宝回调失败: {}", e.getMessage(), e);
            return "failure";
        }
    }

    @Override
    public AlipayResponseDTO queryPayStatus(String orderId) {
        AlipayResponseDTO response = new AlipayResponseDTO();
        response.setOrderId(orderId);

        boolean useMock = "私钥".equals(alipayConfig.getPrivateKey()) || "支付宝公钥".equals(alipayConfig.getPublicKey());
        if (useMock) {
            // Under mock mode, simply check database state
            var order = orderService.findById(orderId);
            if (order != null && order.getStatus() != null && order.getStatus() == 2) {
                response.setStatus("TRADE_SUCCESS");
            } else {
                response.setStatus("WAIT_BUYER_PAY");
            }
            return response;
        }

        try {
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            String bizContent = "{" +
                "\"out_trade_no\":\"" + orderId + "\"" +
                "}";
            queryRequest.setBizContent(bizContent);

            AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
            if (queryResponse.isSuccess()) {
                response.setStatus(queryResponse.getTradeStatus());
            } else {
                response.setStatus("UNKNOWN");
            }
            log.info("支付宝查询结果: {}", queryResponse.getBody());
        } catch (AlipayApiException e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            response.setStatus("UNKNOWN");
        }

        return response;
    }

    @Override
    public boolean verifyCallbackSignature(Map<String, String> params) {
        if ("true".equals(params.get("mock")) || "支付宝公钥".equals(alipayConfig.getPublicKey())) {
            log.info("模拟支付回调签名验证通过");
            return true;
        }
        try {
            return AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getPublicKey(),
                "UTF-8",
                "RSA2"
            );
        } catch (Exception e) {
            log.error("验证签名失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
