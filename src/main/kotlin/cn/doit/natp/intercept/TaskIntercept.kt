@file:Suppress("UNCHECKED_CAST", "unused")

package cn.doit.natp.intercept

import cn.doit.natp.common.BaseMetaObjectIntercept
import com.eova.aop.AopContext
import com.eova.common.plugin.quartz.QuartzPlugin
import com.eova.common.utils.xx
import com.eova.model.Task
import org.quartz.*

class TaskIntercept : BaseMetaObjectIntercept() {

    override fun addAfter(ac: AopContext): String? {
        val className = ac.record.getStr("clazz")
        val exp = ac.record.getStr("exp")
        val clazz = Class.forName(className) as Class<out Job>
        val job = JobBuilder.newJob(clazz).withIdentity(className, className).build()
        val trigger = TriggerBuilder.newTrigger().withIdentity(className, className).withSchedule(CronScheduleBuilder.cronSchedule(exp)).build() as CronTrigger
        QuartzPlugin.scheduler.scheduleJob(job, trigger)
        QuartzPlugin.scheduler.pauseJob(job.key)

        return super.addAfter(ac)
    }

    override fun deleteAfter(ac: AopContext): String? {
        val className = ac.record.getStr("clazz")
        val jobKey = JobKey.jobKey(className, className)
        QuartzPlugin.scheduler.deleteJob(jobKey)
        return super.deleteAfter(ac)
    }

    @Throws(Exception::class)
    override fun updateAfter(ac: AopContext): String? {
        val className = ac.record.getStr("clazz")
        val exp = ac.record.getStr("exp")
        val scheduleBuilder = CronScheduleBuilder.cronSchedule(exp)
        val triggerKey = TriggerKey.triggerKey(className, className)
        var trigger = QuartzPlugin.scheduler.getTrigger(triggerKey) as CronTrigger
        trigger = trigger.triggerBuilder.withIdentity(triggerKey).withSchedule(scheduleBuilder).build() as CronTrigger
        QuartzPlugin.scheduler.rescheduleJob(triggerKey, trigger)
        QuartzPlugin.scheduler.pauseTrigger(triggerKey)
        Task.dao.updateState(xx.toInt(ac.record.get("id"))!!, 0)
        return super.updateAfter(ac)
    }

    @Throws(Exception::class)
    override fun addBefore(ac: AopContext): String? {
        val cs = ac.record.getStr("clazz")
        val flag = Task.dao.isExist("select count(*) from eova_task WHERE clazz = ?", cs)
        return if (flag) {
            throw Exception("Job实现类已经存在:$cs")
        } else {
            null
        }
    }

}