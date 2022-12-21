package com.atguigu.gulimall.search.service;

import com.atguigu.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * ProductSaveService
 *
 * @author fj
 * @date 2022/12/20 22:34
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModel) throws IOException;

}
