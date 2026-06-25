package com.goodskill.ai.service.feign;

import com.goodskill.core.pojo.vo.SeckillVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * 秒杀商品查询服务
 */
@FeignClient(name = "goodskill-seckill", contextId = "ai-seckill-query")
public interface SeckillQueryFeignClient {

    /**
     * 根据秒杀活动id查询商品详情
     */
    @GetMapping("/getById")
    SeckillVO findById(@RequestParam("seckillId") Serializable seckillId);

    /**
     * 获取秒杀活动列表
     */
    @GetMapping("/getSeckillList")
    Object getSeckillList(@RequestParam("pageNum") int pageNum,
                          @RequestParam("pageSize") int pageSize,
                          @RequestParam(value = "goodsName", required = false) String goodsName);

    /**
     * 获取成功秒杀记录数
     */
    @GetMapping("/getSuccessKillCount")
    long getSuccessKillCount(@RequestParam("seckillId") Long seckillId);

}
