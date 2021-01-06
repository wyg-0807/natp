/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.plugin.quartz;

import com.eova.model.Task;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.IPlugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;

public class QuartzPlugin implements IPlugin {

    public static Scheduler scheduler = null;
    private SchedulerFactory sf = null;

    /**
     * 启动Quartz
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean start() {
        // 创建调度工厂
        sf = new StdSchedulerFactory();

        try {
            scheduler = sf.getScheduler();

            List<Task> tasks = Task.dao.findAll();
            for (Task task : tasks) {
                String jobClassName = task.getStr("clazz");
                String jobCronExp = task.getStr("exp");
                int state = task.getInt("state");
                // String params = task.getInt("params");

                Class clazz;
                try {
                    clazz = Class.forName(jobClassName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobClassName, jobClassName).build();
                // job.getJobDataMap().put("type", "eova");
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobClassName).withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp)).build();

                try {
                    scheduler.scheduleJob(job, trigger);
                    if (state == Task.STATE_STOP) {
                        // 暂停触发
                        scheduler.pauseTrigger(trigger.getKey());
                    }
                } catch (SchedulerException e) {
                    new RuntimeException(e);
                }

                LogKit.info(job.getKey() + " loading and exp: " + trigger.getCronExpression());
            }

            scheduler.start();

        } catch (SchedulerException e) {
            new RuntimeException(e);
        }

        return true;

    }

    /**
     * 停止Quartz
     */
    @Override
    public boolean stop() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            LogKit.error("shutdown error", e);
            return false;
        }
        return true;
    }

}