package cn.doit.natp.common

import cn.doit.natp.model.*
import cn.doit.zhangyi.common.model.WxPayment
import com.jfinal.plugin.activerecord.ActiveRecordPlugin

/**
 * @author lzhq
 */
object MainMapping {

    fun addMapping(arp: ActiveRecordPlugin) {
        arp.apply {
            addMapping(Article.TABLE, Article::class.java)
            addMapping(Attachment.TABLE, Attachment::class.java)
            addMapping(Banner.TABLE, Banner::class.java)
            addMapping(BriefNews.TABLE, BriefNews::class.java)
            addMapping(Category.TABLE, Category::class.java)
            addMapping(Department.TABLE, Department::class.java)
            addMapping(Doctor.TABLE, Doctor::class.java)
            addMapping(ErrorLog.TABLE, ErrorLog::class.java)
            addMapping(Guarantor.TABLE, Guarantor::class.java)
            addMapping(KV.TABLE, KV::class.java)
            addMapping(WxSession.TABLE, WxSession::class.java)
            addMapping(NatCollector.TABLE, NatCollector::class.java)
            addMapping(NatExamGroup.TABLE, NatExamGroup::class.java)
            addMapping(NatExamGroupItem.TABLE, NatExamGroupItem::class.java)
            addMapping(NatPatient.TABLE, NatPatient::class.java)
            addMapping(NatReserveLog.TABLE, NatReserveLog::class.java)
            addMapping(Test.TABLE,Test::class.java)
        }
    }

}
