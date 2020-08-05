package com.zgy.dao;

import com.zgy.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
    //后台关于产品展示（分页）
    List<Product> selectList();

    //后台产品搜索
    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);

}