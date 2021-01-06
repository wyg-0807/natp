/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.task;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.plugin.quartz.QuartzPlugin;
import com.eova.common.utils.xx;
import com.eova.model.Task;
import org.quartz.*;

public class TaskIntercept extends MetaObjectIntercept {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public String addAfter(AopContext ac) throws Exception {
        String className = ac.record.getStr("clazz");
        String exp = ac.record.getStr("exp");

        Class clazz = Class.forName(className);
        JobDetail job = JobBuilder.newJob(clazz).withIdentity(className, className).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(className, className).withSchedule(CronScheduleBuilder.cronSchedule(exp)).build();

        // 新增任务
        QuartzPlugin.scheduler.scheduleJob(job, trigger);
        // 默认暂停
        QuartzPlugin.scheduler.pauseJob(job.getKey());

        return null;
    }

    @Override
    public String deleteAfter(AopContext ac) throws Exception {
        String className = ac.record.getStr("clazz");

        // 删除任务(自动删除关联触发器)
        JobKey jobKey = JobKey.jobKey(className, className);

        QuartzPlugin.scheduler.deleteJob(jobKey);

        return null;
    }

    @Override
    public String updateAfter(AopContext ac) throws Exception {
        String className = ac.record.getStr("clazz");
        String exp = ac.record.getStr("exp");

        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(exp);

        // 重新构建触发器覆盖原触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(className, className);
        CronTrigger trigger = (CronTrigger) QuartzPlugin.scheduler.getTrigger(triggerKey);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        // 重置任务
        QuartzPlugin.scheduler.rescheduleJob(triggerKey, trigger);

        // 暂停
        QuartzPlugin.scheduler.pauseTrigger(triggerKey);

        // 状态变更为暂停
        Task.dao.updateState(xx.toInt(ac.record.get("id")), Task.STATE_STOP);

        return null;
    }

    @Override
    public String addBefore(AopContext ac) throws Exception {
        String cs = ac.record.getStr("clazz");
        boolean flag = Task.dao.isExist("select count(*) from eova_task where clazz = ?", cs);
        if (flag) {
            throw new Exception("Job实现类已经存在:" + cs);
        }

        return null;
    }

}