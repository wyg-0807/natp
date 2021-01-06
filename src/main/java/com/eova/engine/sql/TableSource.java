/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.engine.sql;

import java.text.MessageFormat;

/**
 * SQL表相关信息
 *
 * @author Jieven
 */
public class TableSource {

    private String table;
    private String alias;

    private String leftField;
    private String leftAlias;

    private String rigthField;
    private String rigthAlias;

    // private Record data;

    public String getAlias() {
        if (alias == null) {
            return table;
        }
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * 获得当前表的关联字段
     */
    public String getField() {
        if (leftAlias.equals(alias)) {
            return leftField;
        }
        return rigthField;
    }

    public String toString() {
        return MessageFormat.format("{0} as {1} , condition {2}.{3} = {4}.{5}", table, alias, leftAlias, leftField, rigthAlias, rigthField);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getLeftField() {
        return leftField;
    }

    public void setLeftField(String leftField) {
        this.leftField = leftField;
    }

    public String getLeftAlias() {
        return leftAlias;
    }

    public void setLeftAlias(String leftAlias) {
        this.leftAlias = leftAlias;
    }

    public String getRigthField() {
        return rigthField;
    }

    public void setRigthField(String rigthField) {
        this.rigthField = rigthField;
    }

    public String getRigthAlias() {
        return rigthAlias;
    }

    public void setRigthAlias(String rigthAlias) {
        this.rigthAlias = rigthAlias;
    }


}