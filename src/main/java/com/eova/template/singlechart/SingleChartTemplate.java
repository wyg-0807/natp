/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.singlechart;

import com.eova.model.Button;
import com.eova.template.common.config.TemplateConfig;
import com.eova.template.common.util.TemplateUtil;
import com.eova.template.single.SingleTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleChartTemplate extends SingleTemplate {

    @Override
    public String name() {
        return "单表图";
    }

    @Override
    public String code() {
        return TemplateConfig.SINGLE_CHART;
    }

    @Override
    public Map<Integer, List<Button>> getBtnMap() {
        Map<Integer, List<Button>> btnMap = new HashMap<>();

        {
            List<Button> btns = new ArrayList<>();

            btns.add(TemplateUtil.getQueryButton());

            btnMap.put(0, btns);
        }

        return btnMap;
    }

}