/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.type;

import com.eova.common.utils.util.RegexUtil;
import com.eova.model.MetaField;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Oracle数据类型转换器
 *
 * @author Jieven
 */
@SuppressWarnings("rawtypes")
public class OracleConvertor extends Convertor {

    /**
     * 参考：https://blog.csdn.net/honghailiang888/article/details/49464857
     */
    @SuppressWarnings("serial")
    private final static Map<String, Class> map = new HashMap<String, Class>() {
        {
            put("CHAR", java.lang.String.class);
            put("VARCHAR2", java.lang.String.class);
            put("LONG", java.lang.String.class);
            put("NUMBER", java.math.BigDecimal.class);

            put("DATE", java.sql.Date.class);// TODO java.util.Date Oracle Date 可以是 日期 也可以是 时间
            put("TIMESTAMP", java.sql.Timestamp.class);
            put("TIMESTAMP(6)", java.sql.Timestamp.class);// Contributor By QQ:49829050
            put("TIMESTAMP(9)", java.sql.Timestamp.class);// Contributor By QQ:49829050

            //put("BLOB", java.sql.Blob.class);// 冷门
            //put("CLOB", java.sql.Clob.class);// 冷门

            //put("RAW", Byte[].class);// 冷门
            //put("LONGRAW", Byte[].class);// 冷门

            // 冷门类型可自行添加映射规则和转换规则
        }
    };

    /**
     * 值类型强转
     * 因为Oracle所有数字都是Number类型,所以只能根据字符串格式来进行强制的类型纠正
     * 所以并不能保证类型100%如预期一致,如果数据值波动较大,可能一会是Integer,一会是Long
     * 所以如果遇见类型错乱的问题,可尽量使用大类型来接受,比如Number来接受数字,不管是Integer 还是Long都OK.
     * 造成此问题的原因是:因为JFinal为了方便,并没有像Hibernate,Mybatis这些强制弄DB与Java映射配置,所以只能完全依赖JDBC返回类型
     * 如果JDBC不按套路出牌类型就全部乱套了,目前测试下来,Mysql类型都非常准确,而Oracle却存在此问题(仅测过10g,不知道高版本情况如何)
     *
     * @param o    值对象
     * @param type JDBC类型
     * @return
     */
    public static Object convertValue(Object o, int type) {
        if (o == null) {
            return null;
        }

        String s = o.toString();

        // Eova 定制 BOOLEAN规则
        if (type == Types.CHAR && s.length() == 1) {
            return s.equals("1") ? Boolean.TRUE : Boolean.FALSE;
        }
        // Eova 定制 NUMBER规则
        else if (type == Types.NUMERIC) {

            int size = s.length();
            int decimal = 0;

            // 小数
            if (RegexUtil.isTrue("^\\d+\\.\\d+$", s)) {
                String[] ss = s.split("\\.");
                size = ss[0].length();
                decimal = ss[1].length();
            }

            return new OracleConvertor().rule(o, getNumberType(size, decimal));
        }

        return o;
    }

    /**
     * Oracle Number 类型转换规则
     *
     * @param size    整数位
     * @param decimal 小数位
     * @return
     */
    private static Class getNumberType(int size, int decimal) {
        // 整数
        if (decimal == 0) {
            if (size <= 10)// 此处按照使用习惯优先，大多数人的认知是int <= 10位
                return Integer.class;
            if (size <= 18)
                return Long.class;
            return java.math.BigDecimal.class;
        }
        // 小数
        else {
            if (size + decimal <= 8)
                return Float.class;
            if (size + decimal <= 16)
                return Double.class;
            return java.math.BigDecimal.class;
        }

        // 附:常用小数类型简单测试用例(仅以日常习惯作为测试点,没有学术参考性,勿纠结),不满意可自定义转换器
        // String num = "9.8888888888888888888";
        // System.out.println(Float.valueOf(num)); // 输出:99.888885 				8,8-2
        // System.out.println(Double.valueOf(num)); // 输出:99.88888888888889		16,16-2
        // System.out.println(new BigDecimal(num)); // 输出:99.8888888888888888888	支持最大
    }

    @Override
    public Map<String, Class> mapping() {
        return map;
    }

    //	@Override
    //	protected Object rule(Object o, Class c) {

    //		String s = o.toString();

    // Oracle 必须为 java.sql.Date
    //		if (c == java.sql.Date.class) {
    //			// 必须带时分秒
    //			if (s.length() == 10) {
    //				s += " 00:00:00";
    //			}
    //			return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s).getTime());
    //		}
    //		return super.rule(o.toString(), c);
    //
    //	}

    @Override
    public Class getJavaType(MetaField field) {
        String type = field.getDataTypeName();// DB字段类型
        Integer size = field.getInt("data_size");// DB字段长度
        Integer decimal = field.getInt("data_decimal");// DB字段精度

        // Eova Oracle 定制规则 布尔 网络传闻: NUMBER(1)=2bytes CHAR(1)=1bytes
        if (type.equalsIgnoreCase("CHAR") && size == 1) {
            return Boolean.class;
        }

        // Eova Oracle 定制规则 NUMBER 自动转为 常用类型
        if (type.equalsIgnoreCase("NUMBER")) {
            return getNumberType(size, decimal);
        }

        return super.getJavaType(field);
    }

    @Override
    public Object convert(MetaField field, Object o) {
        if (o == null) {
            return null;
        }

        Class clazz = getJavaType(field);

        //		System.out.println(String.format("en=%s dataType=%s javaType=%s data=%s", field.getStr("en"), field.getDataTypeName(), clazz.toString(), o));
        //		System.out.println("转换前类型:" + o.getClass());

        o = rule(o, clazz);

        //		try {
        //			System.out.println("转换后类型:" + o.getClass());
        //		} catch (Exception e) {
        //			System.out.println("转换厚类型:空数据");
        //		}
        //		System.out.println();

        return o;
    }
}