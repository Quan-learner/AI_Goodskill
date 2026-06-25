package com.goodskill.ai.tool;

import com.goodskill.ai.service.feign.SeckillQueryFeignClient;
import com.goodskill.core.pojo.vo.SeckillVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * 查询秒杀商品详情工具
 */
@Component
public class SeckillQueryTool implements Function<SeckillQueryTool.QueryRequest, String> {

    @Autowired
    private SeckillQueryFeignClient seckillQueryFeignClient;

    @Override
    public String apply(QueryRequest request) {
        try {
            if (request.seckillId != null) {
                SeckillVO seckill = seckillQueryFeignClient.findById(request.seckillId);
                if (seckill == null) {
                    return "未找到秒杀活动ID为 " + request.seckillId + " 的商品信息";
                }
                return "秒杀商品详情：\n" +
                        "- 活动ID: " + seckill.getSeckillId() + "\n" +
                        "- 商品名称: " + seckill.getName() + "\n" +
                        "- 库存数量: " + seckill.getNumber() + "\n" +
                        "- 商品价格: " + seckill.getPrice() + "\n" +
                        "- 秒杀开始时间: " + seckill.getStartTime() + "\n" +
                        "- 秒杀结束时间: " + seckill.getEndTime() + "\n" +
                        "- 活动状态: " + seckill.getStatus() + "\n" +
                        "- 创建人: " + seckill.getCreateUser();
            }
            return "请提供秒杀活动ID(seckillId)进行查询";
        } catch (Exception e) {
            return "查询秒杀商品详情失败: " + e.getMessage();
        }
    }

    public record QueryRequest(Long seckillId) {
    }
}
