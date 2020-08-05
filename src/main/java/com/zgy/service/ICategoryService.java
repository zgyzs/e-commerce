package com.zgy.service;

import com.zgy.common.ServerResponse;
import com.zgy.pojo.Category;

import java.util.List;

public interface ICategoryService {
    //增加分类节点
    ServerResponse addCategory(String categoryName, Integer prentId);
    //更新分类名字
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);
    //根据传入的分类id获取该分类下所有下一级分类的信息
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    //递归查询本节点的id及孩子节点的id
     ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
