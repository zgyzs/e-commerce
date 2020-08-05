package com.zgy.dao;

import com.zgy.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    //根据传入的分类id获取该分类下所有平级分类的信息
    List<Category> selectCategoryChildrenByParentId(Integer parentId);


}