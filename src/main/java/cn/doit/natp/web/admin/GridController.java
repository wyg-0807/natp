//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.doit.natp.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.Easy;
import com.eova.common.base.BaseController;
import com.eova.common.render.XlsRender;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.PageConst;
import com.eova.model.EovaLog;
import com.eova.model.Menu;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;
import com.eova.service.sm;
import com.eova.template.common.util.TemplateUtil;
import com.eova.widget.WidgetManager;
import com.eova.widget.WidgetUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author lzhq
 */
@SuppressWarnings({"Duplicates", "unused"})
public class GridController extends BaseController {
    private final Controller ctrl = this;
    private MetaObjectIntercept intercept = null;
    private String errorInfo = "";

    public GridController() {
    }

    private static List<Record> getRecordsByJson(String json, List<MetaField> items, String pkName) {
        List<Record> records = new ArrayList<>();
        List<JSONObject> list = JSON.parseArray(json, JSONObject.class);

        Record re;
        for (Iterator var5 = list.iterator(); var5.hasNext(); records.add(re)) {
            JSONObject o = (JSONObject) var5.next();
            Map<String, Object> map = JSON.parseObject(o + "", new TypeReference<Map<String, Object>>() {
            });
            re = new Record();
            re.setColumns(map);

            MetaField x;
            String en;
            Object value;
            for (Iterator var9 = items.iterator(); var9.hasNext(); re.set(en, EovaConfig.convertor.convert(x, value))) {
                x = (MetaField) var9.next();
                en = x.getEn();
                String exp = x.getStr("exp");
                value = re.get(en);
                if (!xx.isEmpty(exp)) {
                    String valField = en + "_val";
                    value = re.get(valField);
                    re.remove(valField);
                }
            }

            re.remove("pk_val");
            if (xx.isOracle()) {
                re.remove("rownum_");
            }
        }

        return records;
    }

    public void export() throws Exception {
        String objectCode = this.getPara(0);
        String menuCode = this.getPara(1);
        MetaObject object = sm.meta.getMeta(objectCode);
        Menu menu = Menu.dao.findByCode(menuCode);
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        List<Object> paramList = new ArrayList<>();
        String sql = WidgetManager.buildQuerySQL(this.ctrl, menu, object, this.intercept, paramList, true, "");
        Object[] paras = new Object[paramList.size()];
        paramList.toArray(paras);
        List<Record> data = Db.use(object.getDs()).find("select *" + sql, paras);
        if (this.intercept != null) {
            AopContext ac = new AopContext(this.ctrl, data);
            ac.object = object;
            this.intercept.queryAfter(ac);
        }

        List<MetaField> fields = object.getFields();
        WidgetManager.convertValueByExp(this, fields, data);
        Iterator it = fields.iterator();

        while (it.hasNext()) {
            MetaField f = (MetaField) it.next();
            if (!f.getBoolean("is_show")) {
                it.remove();
            }
        }

        this.render(new XlsRender(data, fields, object));
    }

    public void query() throws Exception {
        String objectCode = this.getPara(0);
        String menuCode = this.getPara(1);
        int pageNumber = this.getParaToInt("page", 1);
        int pageSize = this.getParaToInt(PageConst.PAGESIZE, 100000);
        MetaObject object = sm.meta.getMeta(objectCode);
        Menu menu = Menu.dao.findByCode(menuCode);
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        List<Object> paras = new ArrayList<>();
        String select = "select " + WidgetManager.buildSelect(object, this.RID());
        String sql = WidgetManager.buildQuerySQL(this.ctrl, menu, object, this.intercept, paras, true, select);
        Page<Record> page = Db.use(object.getDs()).paginate(pageNumber, pageSize, select, sql, xx.toArray(paras));
        if (this.intercept != null) {
            AopContext ac = new AopContext(this.ctrl, page.getList());
            ac.object = object;
            this.intercept.queryAfter(ac);
        }

        WidgetUtil.copyValueColumn(page.getList(), object.getPk(), object.getFields());
        WidgetManager.convertValueByExp(this, object.getFields(), page.getList());
        String ui = xx.getConfig("ui", "layui");
        switch (ui) {
            case "easyui":
                ui = "{\"total\":%s,\"rows\": %s}";
                break;
            case "layui":
                ui = "{\"code\": 0, \"msg\": \"\", \"count\":\"%s\",\"data\": %s}";
            default:
                break;
        }

        StringBuilder sb = new StringBuilder(String.format(ui, page.getTotalRow(), JsonKit.toJson(page.getList())));
        if (this.intercept != null) {
            AopContext ac = new AopContext(this.ctrl, page.getList());
            ac.object = object;
            Kv footer = this.intercept.queryFooter(ac);
            if (footer != null) {
                sb.insert(sb.length() - 1, String.format(",\"footer\":[%s]", footer.toJson()));
            }
        }

        this.renderJson(sb.toString());
    }

    public void add() throws Exception {
        String objectCode = this.getPara(0);
        final MetaObject object = sm.meta.getMeta(objectCode);
        String json = this.getPara("rows");
        final List<Record> records = getRecordsByJson(json, object.getFields(), object.getPk());
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        Db.use(object.getDs()).tx(() -> {
            try {

                for (Record record : records) {
                    AopContext ac = new AopContext(GridController.this.ctrl, record);
                    ac.object = object;
                    String msg;
                    if (GridController.this.intercept != null) {
                        msg = GridController.this.intercept.addBefore(ac);
                        if (!xx.isEmpty(msg)) {
                            GridController.this.errorInfo = msg;
                            return false;
                        }
                    }

                    if (xx.isEmpty(object.getTable())) {
                        throw new Exception("视图暂时不支持Grid 单元格编辑，请使用Form模式！");
                    }

                    Db.use(object.getDs()).save(object.getTable(), object.getPk(), record);
                    EovaLog.dao.info(GridController.this.ctrl, 1, object.getStr("code"));
                    if (GridController.this.intercept != null) {
                        msg = GridController.this.intercept.addAfter(ac);
                        if (!xx.isEmpty(msg)) {
                            GridController.this.errorInfo = msg;
                            return false;
                        }
                    }
                }

                return true;
            } catch (Exception var5) {
                LogKit.debug(var5.getMessage(), var5);
                GridController.this.errorInfo = TemplateUtil.buildException(var5);
                return false;
            }
        });
        if (this.intercept != null) {
            try {
                AopContext ac = new AopContext(this, records);
                ac.object = object;
                String msg = this.intercept.addSucceed(ac);
                if (!xx.isEmpty(msg)) {
                    this.errorInfo = msg;
                }
            } catch (Exception var7) {
                LogKit.debug(var7.getMessage(), var7);
                this.errorInfo = TemplateUtil.buildException(var7);
            }
        }

        if (!xx.isEmpty(this.errorInfo)) {
            this.renderJson(Easy.fail(this.errorInfo));
        } else {
            this.renderJson(new Easy());
        }
    }

    public void delete() throws Exception {
        this.deleteOrHide(true);
    }

    public void hide() throws Exception {
        this.deleteOrHide(false);
    }

    private void deleteOrHide(final boolean isDel) throws Exception {
        String objectCode = this.getPara(0);
        final MetaObject object = sm.meta.getMeta(objectCode);
        String json = this.getPara("rows");
        final List<Record> records = getRecordsByJson(json, object.getFields(), object.getPk());
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        Db.use(object.getDs()).tx(() -> {
            try {
                for (Record record : records) {
                    AopContext ac = new AopContext(GridController.this.ctrl, record);
                    ac.object = object;
                    String msg;
                    if (GridController.this.intercept != null) {
                        if (isDel) {
                            msg = GridController.this.intercept.deleteBefore(ac);
                        } else {
                            msg = GridController.this.intercept.hideBefore(ac);
                        }

                        if (!xx.isEmpty(msg)) {
                            GridController.this.errorInfo = msg;
                            return false;
                        }
                    }

                    if (!xx.isEmpty(object.getTable())) {
                        msg = object.getPk();
                        String pkValue = record.get(msg).toString();
                        int logType = 3;
                        if (isDel) {
                            Db.use(object.getDs()).deleteById(object.getTable(), msg, pkValue);
                        } else {
                            String hideFieldName = xx.getConfig("hide_field_name", "is_hide");
                            String sql = String.format("update %s set %s = 1 where %s = ?", object.getTable(), hideFieldName, msg);
                            Db.use(object.getDs()).update(sql, pkValue);
                            logType = 5;
                        }

                        EovaLog.dao.info(GridController.this.ctrl, logType, object.getStr("code") + "[" + pkValue + "]");
                    }

                    if (GridController.this.intercept != null) {
                        if (isDel) {
                            msg = GridController.this.intercept.deleteAfter(ac);
                        } else {
                            msg = GridController.this.intercept.hideAfter(ac);
                        }

                        if (!xx.isEmpty(msg)) {
                            GridController.this.errorInfo = msg;
                            return false;
                        }
                    }
                }

                return true;
            } catch (Exception var9) {
                GridController.this.errorInfo = TemplateUtil.buildException(var9);
                return false;
            }
        });
        if (this.intercept != null) {
            try {
                AopContext ac = new AopContext(this, records);
                ac.object = object;
                String msg;
                if (isDel) {
                    msg = this.intercept.deleteSucceed(ac);
                } else {
                    msg = this.intercept.hideSucceed(ac);
                }

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

    public void update() throws Exception {
        String objectCode = this.getPara(0);
        final MetaObject object = sm.meta.getMeta(objectCode);
        String json = this.getPara("rows");
        final List<Record> records = getRecordsByJson(json, object.getFields(), object.getPk());
        this.intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());
        Db.use(object.getDs()).tx(() -> {
            try {

                for (Record record : records) {
                    AopContext ac = new AopContext(GridController.this.ctrl, record);
                    ac.object = object;
                    String msg;
                    if (GridController.this.intercept != null) {
                        msg = GridController.this.intercept.updateBefore(ac);
                        if (!xx.isEmpty(msg)) {
                            GridController.this.errorInfo = msg;
                            return false;
                        }
                    }

                    if (xx.isEmpty(object.getTable())) {
                        throw new Exception("视图暂时不支持Grid单元格编辑，请使用Form模式！");
                    }

                    Db.use(object.getDs()).update(object.getTable(), object.getPk(), record);
                    EovaLog.dao.info(GridController.this.ctrl, 2, object.getStr("code") + "[" + record.get(object.getPk()) + "]");
                    if (GridController.this.intercept != null) {
                        msg = GridController.this.intercept.updateAfter(ac);
                        if (!xx.isEmpty(msg)) {
                            GridController.this.errorInfo = msg;
                            return false;
                        }
                    }
                }

                return true;
            } catch (Exception var5) {
                LogKit.debug(var5.getMessage(), var5);
                GridController.this.errorInfo = TemplateUtil.buildException(var5);
                return false;
            }
        });
        if (this.intercept != null) {
            try {
                AopContext ac = new AopContext(this, records);
                ac.object = object;
                String msg = this.intercept.updateSucceed(ac);
                if (!xx.isEmpty(msg)) {
                    this.errorInfo = msg;
                }
            } catch (Exception var7) {
                LogKit.debug(var7.getMessage(), var7);
                this.errorInfo = TemplateUtil.buildException(var7);
            }
        }

        if (!xx.isEmpty(this.errorInfo)) {
            this.renderJson(Easy.fail(this.errorInfo));
        } else {
            this.renderJson(new Easy());
        }
    }

    public void updateWidths() {
        String objectCode = this.getPara(0);
        if (objectCode == null) {
            this.renderJson(Easy.fail("元对象缺失,当前组件无法调整列宽!"));
        } else {
            String widths = this.getPara(1);
            String[] ss = widths.split(",");
            int i = 0;
            List<MetaField> fields = MetaField.dao.queryShowFieldByObjectCode(objectCode);

            for (Iterator var6 = fields.iterator(); var6.hasNext(); ++i) {
                MetaField x = (MetaField) var6.next();
                x.set("width", ss[i]);
                x.update();
            }

            this.renderJson(Easy.sucess());
        }
    }

    public void updateCell() {
        String code = this.getPara("code");
        String pk = this.getPara("pk");
        String field = this.getPara("field");
        String val = this.getPara("val");
        MetaObject object = sm.meta.getMeta(code);
        Db.use(object.getDs()).update(String.format("update %s set %s = ? where %s = ?", object.getTable(), field, object.getPk()), val, pk);
        this.renderJson(Easy.sucess());
    }
}
