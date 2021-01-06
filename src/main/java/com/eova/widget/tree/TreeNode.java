/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget.tree;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Tree Node VO
 *
 * @author Jieven
 * @date 2014-9-8
 */
public class TreeNode extends Record {

    private static final long serialVersionUID = -5190761342805087001L;

    // 子节点
    private List<TreeNode> childs;

    public List<TreeNode> getChildList() {
        return childs;
    }

    public void setChildList(List<TreeNode> childList) {
        this.childs = childList;
    }

}