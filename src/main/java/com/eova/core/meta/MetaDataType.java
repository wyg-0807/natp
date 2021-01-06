/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.meta;

import com.eova.common.utils.xx;
import com.eova.model.MetaField;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * db types <> java types
 *
 * @author Jieven
 */
@SuppressWarnings("rawtypes")
public class MetaDataType {

    /**
     * 参考：http://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html INT UNSIGNED 这里强制指定为 Integer 因为大部分人不知道应该为Long
     */
    @SuppressWarnings("serial")
    private final static Map<String, Class> map = new HashMap<String, Class>() {
        {
            // MySQL
            put("BIT", Boolean.class);
            put("TEXT", String.class);

            put("DATE", java.util.Date.class);
            put("DATETIME", DateTime.class);
            put("TIMESTAMP", java.sql.Timestamp.class);
            put("TIME", java.sql.Time.class);

            put("TINYINT", Integer.class);
            put("SMALLINT", Integer.class);
            put("MEDIUMINT", Integer.class);
            put("INT", Integer.class);
            put("BIGINT", Long.class);
            put("SMALLINT UNSIGNED", Integer.class);
            put("MEDIUMINT UNSIGNED", Integer.class);
            // mysql if UNSIGNED Long, because eova the most easy! if the overflow,you should use bigint!
            put("INT UNSIGNED", Integer.class);
            put("BIGINT UNSIGNED", BigInteger.class);
            put("FLOAT", Float.class);
            put("DOUBLE", Double.class);
            put("DECIMAL", BigDecimal.class);

            put("CHAR", String.class);
            put("VARCHAR", String.class);
            put("BINARY", Byte[].class);
            put("VARBINARY", Byte[].class);
            put("TINYBLOB", Byte[].class);
            put("VARCHAR", String.class);
            put("BLOB", Byte[].class);
            put("VARCHAR", String.class);
            put("MEDIUMBLOB", Byte[].class);
            put("VARCHAR", String.class);
            put("LONGBLOB", Byte[].class);
            put("VARCHAR", String.class);

            // Oracle
            put("VARCHAR2", String.class);
            put("LONG", String.class);
            put("NUMBER", BigDecimal.class);
            put("CLOB", Clob.class);
            put("TIMESTAMP(6)", java.sql.Timestamp.class);

            // PostgreSQL

            // SqlServer
            //			put("MONEY", BigDecimal.class);
            //			put("SMALLMONEY", BigDecimal.class);
            //			put("FLOAT", Double.class);
            //
            //			put("VARBINARY", Byte[].class);
            //			put("NVARCHAR", String.class);
            //			put("NTEXT", String.class);
            //			put("NUMERIC", BigDecimal.class);
            //
            //			put("SMALLINT", Short.class);
            //			put("SMALLDATETIME", java.sql.Timestamp.class);

            //			put("DATETIME", java.sql.Timestamp.class);
            //			put("TIME", java.sql.Time.class);
            //			put("DATE", java.sql.Date.class);
            //			put("DATETIME2", java.sql.Timestamp.class);

            //			put("DECIMAL() IDENTITY", Integer.class);
        }
    };
    private static final DateTimeFormatter forPattern = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static Class getType(String dataType) {
        return map.get(dataType);
    }

    public static Object convert(MetaField field, Object o) {
        if (o == null) {
            return null;
        }
        String typeName = field.getDataTypeName();
        Integer size = field.getInt("data_size");
        Integer decimal = field.getInt("data_decimal");
        // System.out.println(String.format("en:%s type:%s", field.getStr("en"), typeName));
        Class clazz = getType(typeName);

        // DB类型特殊转换规则
        if (xx.isMysql()) {
            if (typeName.equalsIgnoreCase("TINYINT") && size == 1) {
                clazz = Boolean.class;
            }
        } else if (xx.isOracle()) {
            if (typeName.equalsIgnoreCase("CHAR") && size == 1) {
                clazz = Boolean.class;
            } else if (typeName.equalsIgnoreCase("NUMBER")) {
                if (decimal == 0) {
                    if (size <= 10) {
                        clazz = Integer.class;
                    } else {
                        clazz = Long.class;
                    }
                } else {
                    if (size <= 4) {
                        clazz = Float.class;
                    } else {
                        clazz = Double.class;
                    }
                }
            } else if (typeName.equalsIgnoreCase("DATE")) {
                if (o.toString().length() > 10) {
                    clazz = java.sql.Timestamp.class;
                }
            }
        }

        // System.out.println(field.getEn() + " ");
        // System.out.println(o.getClass());
        o = cast(o.toString(), clazz);
        // try {
        // System.out.println("\t" + o.getClass());
        // } catch (Exception e) {
        // System.out.println("\tNull");
        // }
        // System.out.println();
        return o;
    }

    public static Object cast(String s, Class c) {
        if (s == null) {
            return null;
        }

        // s = s.trim();不能这样搞,文本域头部可能有空格
        if (s.length() == 0 && c != String.class) {
            // empty string only cast to string.class
            return null;
        }

        if (c == Integer.class) {
            return Integer.parseInt(s);
        }
        if (c == Long.class) {
            return Long.parseLong(s);
        }
        if (c == Float.class) {
            return Float.parseFloat(s);
        }
        if (c == Double.class) {
            return Double.parseDouble(s);
        }
        if (c == Boolean.class) {
            if (xx.isNum(s)) {
                if (s.equals("1")) {
                    s = "true";
                } else {
                    s = "false";
                }
            }
            return Boolean.parseBoolean(s);
        }
        if (c == BigInteger.class) {
            return BigInteger.valueOf(Long.parseLong(s));
        }
        if (c == BigDecimal.class) {
            return BigDecimal.valueOf(Double.parseDouble(s));
        }
        if (c == Byte[].class) {
            return s.getBytes();
        }
        try {
            if (c == Timestamp.class) {
                long time = DateTime.parse(s, forPattern).getMillis();
                return new Timestamp(time);
            }
            if (c == DateTime.class) {
                if (xx.isOracle()) {
                    long time = DateTime.parse(s, forPattern).getMillis();
                    return new Timestamp(time);
                } else {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
                }
            }
            if (c == java.sql.Date.class) {
                return java.sql.Date.valueOf(s);
            }
            if (xx.isOracle() && c == java.util.Date.class) {
                return java.sql.Date.valueOf(s);
            }
            if (c == Time.class) {
                return new SimpleDateFormat("HH:mm:ss").parse(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return s;
    }

}