package com.zgy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zgy.common.ResponseCode;
import com.zgy.common.ServerResponse;
import com.zgy.dao.CategoryMapper;
import com.zgy.dao.ProductMapper;
import com.zgy.pojo.Category;
import com.zgy.pojo.Product;
import com.zgy.service.IPorductService;
import com.zgy.util.DateTimeUtil;
import com.zgy.util.PropertiesUtil;
import com.zgy.vo.ProductDetailVo;
import com.zgy.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IPorductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    //保存（新增）产品或者更新产品
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product != null){
            //判断对象是否为空
            if(StringUtils.isNotBlank(product.getSubImages())){
                //判断子图是否为空 如果不为空 用“”，“分割
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length>0){
                    //判断子图是否大于0 如果大于0 吧子图中的第一个图片当做主图
                    product.setMainImage(subImageArray[0]);
                }
            }
            //和前端约定 如果是更新 prodecuct的id不为空 如果是新增prodecuct的id为空  判断是更新还是新增
            if(product.getId()!=null){
                //如果是更新
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }else {
                //不是更新 那就是新增
                int rowCount = productMapper.insert(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }

        }

        return  ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    //更新产品状态
    public ServerResponse<String> setSaleStatus(Integer prductId,Integer status){
        if (prductId == null || status == null){
             return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(prductId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    //获取产品详情
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //产品详情封装的vo对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return  ServerResponse.createBySuccess(productDetailVo);

    }
    //封装查询产品详细信息 ProductDetailVo
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(productDetailVo.getId());
        productDetailVo.setSubtitle(productDetailVo.getSubtitle());
        productDetailVo.setPrice(productDetailVo.getPrice());
        productDetailVo.setMainImage(productDetailVo.getMainImage());
        productDetailVo.setSubImages(productDetailVo.getSubImages());
        productDetailVo.setCategoryId(productDetailVo.getCategoryId());
        productDetailVo.setDetail(productDetailVo.getDetail());
        productDetailVo.setName(productDetailVo.getName());
        productDetailVo.setStatus(productDetailVo.getStatus());
        productDetailVo.setStock(productDetailVo.getStatus());

        //imageHost使用配置文件方式读取，方便维护
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParenCategoryId(0);//默认根节点
        }else {
            productDetailVo.setParenCategoryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return  productDetailVo;
    }

    //后台关于产品展示（分页） pageNum表示分页第几页 pageSize页面容量（显示多少条数据）
    public  ServerResponse<PageInfo> getProdctList(int pageNum,int pageSize){
        //startPage-start配置
        PageHelper.startPage(pageNum,pageSize);
        //填充自己的sql查询逻辑
        List<Product> productList = productMapper.selectList();
        //吧 Product 转为 ProductListVo
        ArrayList<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //pageHelper收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return  ServerResponse.createBySuccess(pageResult);
    }
    //产品信息展示（分页） ProductListVo
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCatgoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    //后台产品搜索
    public ServerResponse<PageInfo> searchProduct(String prdcuctName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(prdcuctName)){
            prdcuctName  = new StringBuilder().append("%").append(prdcuctName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(prdcuctName, productId);
        //吧 Product 转为 ProductListVo
        ArrayList<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //pageHelper收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return  ServerResponse.createBySuccess(pageResult);
    }
}
