package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * MallSearchService
 *
 * @author fj
 * @date 2022/12/24 14:13
 */
public interface MallSearchService {

    SearchResult search(SearchParam param);
}
