/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.task;

import com.eova.common.Easy;
import com.eova.common.plugin.quartz.QuartzPlugin;
import com.eova.model.Task;
import com.jfinal.core.Controller;
import org.quartz.JobKey;

/**
 * 定时任务
 *
 * @author Jieven
 */
public class TaskController extends Controller {

    // 启动任务
    public void start() {
        int id = getParaToInt(0);

        Task task = Task.dao.findById(id);

        try {
            String className = task.getStr("clazz");

            // 恢复任务
            JobKey jobKey = JobKey.jobKey(className, className);
            QuartzPlugin.scheduler.resumeJob(jobKey);

            Task.dao.updateState(id, Task.STATE_START);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(new Easy("任务启动失败！"));
        }

        renderJson(new Easy());
    }

    // 暂停任务
    public void stop() {
        int id = getParaToInt(0);

        Task task = Task.dao.findById(id);

        try {
            String className = task.getStr("clazz");

            // 暂停任务
            JobKey jobKey = JobKey.jobKey(className, className);
            QuartzPlugin.scheduler.pauseJob(jobKey);

            Task.dao.updateState(id, Task.STATE_STOP);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(new Easy("任务停止失败！"));
        }

        renderJson(new Easy());
    }

    // 立即运行一次任务
    public void trigger() {
        int id = getParaToInt(0);

        Task task = Task.dao.findById(id);

        try {
            String className = task.getStr("clazz");

            // 立即触发一次
            JobKey jobKey = JobKey.jobKey(className, className);
            QuartzPlugin.scheduler.triggerJob(jobKey);

        } catch (Exception e) {
            e.printStackTrace();
            renderJson(new Easy("任务停止失败！"));
        }

        renderJson(new Easy());
    }

}