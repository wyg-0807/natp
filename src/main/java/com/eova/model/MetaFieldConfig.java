/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.model;

/**
 * 元字段配置
 *
 * @author Jieven
 */
public class MetaFieldConfig {

    // 文件保存目录
    private String filedir;
    // 固定文件名(会自动覆盖同名文件)
    private String filename;
    // 备注
    private String memo;
    // 图片宽度(多图)
    private String width;
    // 图片高度(多图)
    private String height;

    // 是否进行合计
    private boolean totalRow = false;

    public String getFiledir() {
        return filedir;
    }

    public void setFiledir(String filedir) {
        this.filedir = filedir;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public boolean isTotalRow() {
        return totalRow;
    }

    public void setTotalRow(boolean totalRow) {
        this.totalRow = totalRow;
    }

}