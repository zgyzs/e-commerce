package com.zgy.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

//处理用户标识有效期
public class TokenCache {
    //处理日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX = "token_";
    //本地缓存 initialCapacity的参数代表设置缓存的初始化容量
    // maximumSize的参数代表缓存的最大容量 当超过最大容量时 使用LRU算法（最少使用算法）移除缓存项
    //expireAfterAccess的参数表示有效期
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().
            initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).
            build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的取值，就调用这个方法进行加载。
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });



    public static void setKey(String key,String value){
        localCache.put(key, value);
    }

    public static String getKey(String key){
        String value = "null";
        try {
             value = localCache.get(key);
             if("null".equals(value)){
                 return null;
             }
             return value;
        }catch (Exception e){
            logger.error("localCache get errier",e);
        }
        return  null;
    }
}
