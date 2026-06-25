package com.goodskill.service.impl;

import com.goodskill.core.pojo.dto.GoodsDTO;
import com.goodskill.service.inner.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@ConditionalOnMissingBean(SeckillGoodsServiceImpl.class)
public class NoOpSeckillGoodsServiceImpl implements SeckillGoodsService {
    @Override
    public void save(GoodsDTO goodsDto) {
        log.warn("ES服务不可用，跳过商品保存到ES");
    }

    @Override
    public void saveBatch(List<GoodsDTO> list) {
        log.warn("ES服务不可用，跳过批量商品保存到ES");
    }

    @Override
    public void delete(GoodsDTO goodsDto) {
        log.warn("ES服务不可用，跳过商品从ES删除");
    }

    @Override
    public List<GoodsDTO> searchWithNameByPage(String input) {
        log.warn("ES服务不可用，无法搜索商品");
        return Collections.emptyList();
    }
}
