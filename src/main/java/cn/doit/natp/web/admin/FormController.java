package cn.doit.natp.web.admin;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.Easy;
import com.eova.common.utils.xx;
import com.eova.model.EovaLog;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;
import com.eova.service.sm;
import com.eova.template.common.util.TemplateUtil;
import com.eova.widget.WidgetManager;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;

/**
 * @author lzhq
 */
public class FormController extends Controller {
    private final Controller ctrl = this;
    private final Record record = new Record();
    private MetaObjectIntercept intercept = null;
    private String errorInfo = "";

    public FormController() {
    }

    public void add() throws Exception {
        String objectCode = this.getPara(0);
        MetaObject object = sm.meta.getMeta(objectCode);
        for (MetaField mf : object.getFields()) {
            mf.put("is_disable", false);
        }

        Record fixed = WidgetManager.getRef(this);
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        if (this.intercept != null) {
            AopContext ac = new AopContext(this.ctrl);
            ac.object = object;
            ac.fixed = fixed;
            this.intercept.addInit(ac);
        }

        this.setAttr("fixed", fixed);
        this.setAttr("object", object);
        this.render("/eova/widget/form/add.html");
    }

    public void doAdd() throws Exception {
        String objectCode = this.getPara(0);
        final MetaObject object = sm.meta.getMeta(objectCode);
        WidgetManager.buildData(this, object, this.record, object.getPk(), true);
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        boolean isSucceed = Db.use(object.getDs()).tx(() -> {
            try {
                AopContext ac = new AopContext(ctrl, record);
                ac.object = object;
                String msg;
                if (intercept != null) {
                    msg = intercept.addBefore(ac);
                    if (!xx.isEmpty(msg)) {
                        errorInfo = msg;
                        return false;
                    }
                }

                if (!xx.isEmpty(object.getTable())) {
                    Record t = WidgetManager.peelVirtual(record);
                    Db.use(object.getDs()).save(object.getTable(), object.getPk(), record);
                    record.setColumns(t);
                }

                if (intercept != null) {
                    msg = intercept.addAfter(ac);
                    if (!xx.isEmpty(msg)) {
                        errorInfo = msg;
                        return false;
                    }
                }

                return true;
            } catch (Exception var3) {
                errorInfo = TemplateUtil.buildException(var3);
                return false;
            }
        });
        EovaLog.dao.info(this, 1, object.getStr("code"));
        if (isSucceed && this.intercept != null) {
            try {
                ArrayList<Record> records = new ArrayList<>();
                records.add(this.record);
                AopContext ac = new AopContext(this, records);
                ac.object = object;
                String msg = this.intercept.addSucceed(ac);
                if (!xx.isEmpty(msg)) {
                    this.errorInfo = msg;
                }
            } catch (Exception var7) {
                this.errorInfo = TemplateUtil.buildException(var7);
            }
        }

        if (!xx.isEmpty(this.errorInfo)) {
            this.renderJson(Easy.fail(this.errorInfo));
        } else {
            this.renderJson(new Easy());
        }
    }

    public void update() throws Exception {
        Record fixed = WidgetManager.getRef(this);
        AopContext ac = new AopContext(this.ctrl);
        ac.fixed = fixed;
        MetaObject object = this.buildFormData(ac);
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        if (this.intercept != null) {
            this.intercept.updateInit(ac);
        }

        this.setAttr("fixed", fixed);
        this.render("/eova/widget/form/update.html");
    }

    public void doUpdate() throws Exception {
        String objectCode = this.getPara(0);
        final MetaObject object = sm.meta.getMeta(objectCode);
        WidgetManager.buildData(this, object, this.record, object.getPk(), false);
        Object pkValue = this.record.get(object.getPk());
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        boolean isSucceed = Db.use(object.getDs()).tx(() -> {
            try {
                AopContext ac = new AopContext(ctrl, record);
                ac.object = object;
                String msg;
                if (intercept != null) {
                    msg = intercept.updateBefore(ac);
                    if (!xx.isEmpty(msg)) {
                        errorInfo = msg;
                        return false;
                    }
                }

                if (!xx.isEmpty(object.getTable())) {
                    Record t = WidgetManager.peelVirtual(record);
                    Db.use(object.getDs()).update(object.getTable(), object.getPk(), record);
                    record.setColumns(t);
                }

                if (intercept != null) {
                    msg = intercept.updateAfter(ac);
                    if (!xx.isEmpty(msg)) {
                        errorInfo = msg;
                    }
                }

                return true;
            } catch (Exception var3) {
                errorInfo = TemplateUtil.buildException(var3);
                return false;
            }
        });
        EovaLog.dao.info(this, 2, object.getStr("code") + "[" + pkValue + "]");
        if (isSucceed && this.intercept != null) {
            try {
                ArrayList<Record> records = new ArrayList<>();
                records.add(this.record);
                AopContext ac = new AopContext(this, records);
                ac.object = object;
                String msg = this.intercept.updateSucceed(ac);
                if (!xx.isEmpty(msg)) {
                    this.errorInfo = msg;
                }
            } catch (Exception var8) {
                this.errorInfo = TemplateUtil.buildException(var8);
            }
        }

        if (!xx.isEmpty(this.errorInfo)) {
            this.renderJson(Easy.fail(this.errorInfo));
        } else {
            this.renderJson(new Easy());
        }
    }

    public void detail() throws Exception {
        AopContext ac = new AopContext(this.ctrl);
        MetaObject object = this.buildFormData(ac);
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        if (this.intercept != null) {
            this.intercept.updateInit(ac);
        }

        this.render("/eova/widget/form/detail.html");
    }

    private MetaObject buildFormData(AopContext ac) {
        String objectCode = this.getPara(0);
        Object pkValue = this.getPara(1);
        MetaObject object = sm.meta.getMeta(objectCode);
        ac.object = object;
        Record record = Db.use(object.getDs()).findById(object.getView(), object.getPk(), pkValue);
        String eovaObjectCode = "eova_object_code";
        if (eovaObjectCode.equals(objectCode) && !xx.isNum(pkValue)) {
            MetaObject mo = MetaObject.dao.getByCode(pkValue.toString());
            record = (new Record()).setColumns(mo);
        }

        this.setAttr("record", record);
        this.setAttr("object", object);
        ac.record = record;
        return object;
    }
}
