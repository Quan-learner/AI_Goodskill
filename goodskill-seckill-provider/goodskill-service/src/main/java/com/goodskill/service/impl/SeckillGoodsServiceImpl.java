package com.goodskill.service.impl;

import com.goodskill.core.pojo.dto.GoodsDTO;
import com.goodskill.service.es.model.Goods;
import com.goodskill.service.es.repository.GoodsRepository;
import com.goodskill.service.inner.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品信息es库操作类
 *
 * @author techa03
 * @date 2019/6/15
 */
@Service
@Slf4j
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    private BeanCopier beanCopier;
    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired(required = false)
    private GoodsRepository goodsRepository;

    private BeanCopier getBeanCopier() {
        if (beanCopier == null) {
            beanCopier = BeanCopier.create(GoodsDTO.class, Goods.class, false);
        }
        return beanCopier;
    }

    @Override
    public void save(GoodsDTO goodsDto) {
        if (goodsRepository == null) {
            log.warn("ES服务不可用，跳过商品保存");
            return;
        }
        Goods goods = new Goods();
        getBeanCopier().copy(goodsDto, goods, null);
        goodsRepository.save(goods);
    }

    @Override
    public void saveBatch(List<GoodsDTO> list) {
        if (goodsRepository == null) {
            log.warn("ES服务不可用，跳过批量商品保存");
            return;
        }
        List<Goods> collect = list.stream().map(dto -> {
            Goods goods = new Goods();
            getBeanCopier().copy(dto, goods, null);
            return goods;
        }).collect(Collectors.toList());
        goodsRepository.saveAll(collect);
    }

    @Override
    public void delete(GoodsDTO goodsDto) {
        if (goodsRepository == null) {
            log.warn("ES服务不可用，跳过商品删除");
            return;
        }
        goodsRepository.deleteByGoodsId(goodsDto.getGoodsId());
    }

    @Override
    public List<GoodsDTO> searchWithNameByPage(String input) {
        if (elasticsearchOperations == null) {
            log.warn("ES服务不可用，无法搜索商品");
            return Collections.emptyList();
        }
        Criteria criteria = new Criteria("name").matches(input);
        Query query = new CriteriaQuery(criteria);

        HighlightFieldParameters parameters = HighlightFieldParameters.builder()
                .withPostTags(new String[] { "</font>" })
                .withPreTags(new String[] { "<font color='red'>" })
                .build();
        HighlightField highlightField = new HighlightField("name", parameters);
        Highlight highlight = new Highlight(HighlightParameters.builder().build(), List.of(highlightField));
        query.setHighlightQuery(new HighlightQuery(highlight, null));
        query.setPageable(PageRequest.of(0, 3));
        return elasticsearchOperations.search(query, Goods.class)
                .getSearchHits().stream().map(s -> {
                    Goods goods = s.getContent();
                    GoodsDTO goodsDto = new GoodsDTO();
                    goodsDto.setName(s.getHighlightField("name").getFirst());
                    goodsDto.setRawName(goods.getName());
                    return goodsDto;
                }).collect(Collectors.toList());
    }
}
