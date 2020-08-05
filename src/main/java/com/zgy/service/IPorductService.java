package com.zgy.service;

import com.github.pagehelper.PageInfo;
import com.zgy.common.ServerResponse;
import com.zgy.pojo.Product;
import com.zgy.vo.ProductDetailVo;

public interface IPorductService {
    //保存（新增）产品或者更新产品
    ServerResponse saveOrUpdateProduct(Product product);
    //更新产品状态F
     ServerResponse<String> setSaleStatus(Integer prductId,Integer status);
    //获取产品详情
     ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    //后台关于产品展示（分页） pageNum表示分页第几页 pageSize页面容量（显示多少条数据）
    ServerResponse<PageInfo> getProdctList(int pageNum, int pageSize);
    //后台产品搜索
     ServerResponse<PageInfo> searchProduct(String prdcuctName,Integer productId,int pageNum,int pageSize);

}
