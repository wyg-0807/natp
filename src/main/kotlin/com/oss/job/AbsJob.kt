package com.oss.job

import com.jfinal.kit.LogKit
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

/**
 * 使用代理人模式记录 Job 执行中遇到的错误信息。
 */
abstract class AbsJob : Job {

    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        val name = this.javaClass.name
        LogKit.info("Job Start:$name")

        try {
            process(context)
        } catch (e: Exception) {
            LogKit.info("业务执行异常：", e)
        }

        LogKit.info("Job End:$name")
    }

    abstract fun process(context: JobExecutionContext)

}