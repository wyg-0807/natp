/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.base;

import com.jfinal.plugin.ehcache.CacheKit;

/**
 * Cache 公共实现
 *
 * @author Jieven
 */
public class BaseCache {

    /**
     * 本系统默认CacheName-空闲30Min超时,60Min有效,最少使用策略
     **/
    public static final String SYS = "sys";
    /**
     * Service CacheName-10s有效,最近最少使用策略
     **/
    public static final String SER = "service";
    /**
     * Player CacheName-空闲30Min超时,永久有效,最少使用策略
     **/
    public static final String PLAYER = "player";

    private static Object getCache(String cacheName, String key) {
        return CacheKit.get(cacheName, key);
    }

    // System Cache方法
    public static Object get(String key) {
        return getCache(SYS, key);
    }

    public static void put(String key, Object value) {
        CacheKit.put(SYS, key, value);
    }

    public static void del(String key) {
        CacheKit.remove(SYS, key);
    }

    // Service Cache方法
    public static Object getSer(String key) {
        return getCache(SER, key);
    }

    public static void putSer(String key, Object value) {
        CacheKit.put(SER, key, value);
    }

    public static void delSer(String key) {
        CacheKit.remove(SER, key);
    }
}