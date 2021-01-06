/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.util;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * 是否匹配到结果
     *
     * @param regex 正则表达式
     * @param str   目标字符串
     * @return
     */
    public static boolean isExist(String regex, String str) {
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(str);
        return mat.find();
    }

    /**
     * 正则替换(默认不区分大小写)
     *
     * @param regex 正则表达式
     * @param ment  替换内容
     * @param str   目标字符串
     * @return 替换后字符串
     */
    public static String replaceAll(String regex, String ment, String str) {
        return replaceAll(regex, ment, str, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 正则替换
     *
     * @param regex 正则表达式
     * @param ment  替换内容
     * @param str   目标字符串
     * @param flags 匹配模式
     *              Pattern.CASE_INSENSITIVE忽略大小写
     * @return 替换后字符串
     */
    public static String replaceAll(String regex, String ment, String str, int flags) {
        Pattern pat = null;
        if (flags == -1) {
            pat = Pattern.compile(regex);
        } else {
            pat = Pattern.compile(regex, flags);
        }
        Matcher mat = pat.matcher(str);
        return mat.replaceAll(ment);
    }

    /**
     * 获取匹配参数值 eg: regex=(.*)[b](.*)[/b],str=A[b]B[/b],return [1]=A,[2]=B
     *
     * @param regex 正则表达式
     * @param str   目标字符串
     * @return 匹配参数值
     */
    public static String[] getMatcherValue(String regex, String str) {
        return getMatcherValue(regex, str, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 获取匹配参数值
     *
     * @param regex 正则表达式
     * @param str   目标字符串
     * @param flags 匹配模式
     *              Pattern.CASE_INSENSITIVE忽略大小写
     * @return
     */
    public static String[] getMatcherValue(String regex, String str, int flags) {
        Pattern pat = null;
        if (flags == -1) {
            pat = Pattern.compile(regex);
        } else {
            pat = Pattern.compile(regex, flags);
        }
        Matcher mat = pat.matcher(str);
        if (mat.find()) {
            String[] param = new String[mat.groupCount()];
            for (int i = 0; i < mat.groupCount(); i++) {
                param[i] = mat.group(i + 1);
            }
            return param;
        }
        return null;
    }

    /**
     * 是否符合正则判定
     *
     * @param regex
     * @param str
     * @return
     */
    public static boolean isTrue(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 正则提取中文
     *
     * @param str
     * @return
     */
    public static HashSet<String> getChinese(String str) {
        HashSet<String> cns = new HashSet<>();
        String regex = "([\u4e00-\u9fa5]+)";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            cns.add(matcher.group(0));
        }
        return cns;
    }
}