package cn.doit.natp.web.api

import cn.doit.natp.model.Test
import com.jfinal.aop.Before
import com.jfinal.aop.Clear
import com.jfinal.core.Controller
import com.jfinal.core.paragetter.Para
import com.jfinal.ext.interceptor.GET
import com.jfinal.ext.interceptor.POST
import com.jfinal.kit.HttpKit

class TestController : Controller() {
    @Before(POST::class)
    @Clear(value = [WxSessionInterceptor::class,PatientInterceptor::class])
    fun save(
            @Para("id") id: Int, @Para("name") name: String,
            @Para("age") age: Int, @Para("num") num: Int) {

        val test :Test = Test().apply {
            this.id = id
            this.name = name
            this.age = age
            this.num = num
        }

        renderJson(test.save())

//        renderJson(test.delete())
//
//        renderJson(test.update())

    }

    @Before(POST::class)
    @Clear(value = [WxSessionInterceptor::class,PatientInterceptor::class])
    fun delete(@Para("id") id: Int){
        renderJson(Test.DAO.deleteById(id))
    }

    @Before(POST::class)
    @Clear(value = [WxSessionInterceptor::class,PatientInterceptor::class])
    fun update(@Para("id") id: Int , @Para("name") name: String, @Para("age") age: Int, @Para("num") num: Int){
        var test = Test.DAO.findById(id)
        test.apply {
            test.name = name
            this.age = age
            this.num = num
        }
        //println(HttpKit.readData(getRequest())+"=============================")
        renderJson(test.update())
    }

    @Before(GET::class)
    @Clear(value = [WxSessionInterceptor::class,PatientInterceptor::class])
    fun find(@Para("id") id: Int) {
        renderJson(Test.DAO.findById(id))
    }

    @Before(GET::class)
    @Clear(value = [WxSessionInterceptor::class,PatientInterceptor::class])
    fun findAll() {
        renderJson(Test.DAO.findAll())
    }

}
