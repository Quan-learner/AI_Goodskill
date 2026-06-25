package com.goodskill.service.controller;

import com.goodskill.core.pojo.dto.GoodsDTO;
import com.goodskill.service.inner.SeckillGoodsService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/es")
@ConditionalOnBean(ElasticsearchOperations.class)
public class GoodsEsController {

    @Resource
    private SeckillGoodsService seckillGoodsService;

    @PutMapping("/save")
    public void save(@RequestBody GoodsDTO goodsDto) {
        seckillGoodsService.save(goodsDto);
    }

    @PutMapping("/saveBatch")
    public void saveBatch(@RequestBody List<GoodsDTO> list) {
        seckillGoodsService.saveBatch(list);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestBody GoodsDTO goodsDto) {
        seckillGoodsService.delete(goodsDto);
    }

    @GetMapping("/searchWithNameByPage")
    public List<GoodsDTO> searchWithNameByPage(@RequestParam("input") String input) {
        return seckillGoodsService.searchWithNameByPage(input);
    }
}
