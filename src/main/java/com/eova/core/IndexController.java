/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core;

import com.eova.common.Easy;
import com.eova.common.base.BaseController;
import com.eova.common.utils.EncryptUtil;
import com.eova.common.utils.db.JdbcUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.EovaConst;
import com.eova.copyright.EovaAuth;
import com.eova.i18n.I18NBuilder;
import com.eova.model.Button;
import com.eova.model.Menu;
import com.eova.model.User;
import com.eova.service.sm;
import com.eova.widget.WidgetUtil;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.text.MessageFormat;
import java.util.*;

/**
 * Eova入口
 *
 * @author Jieven
 * @date 2015-1-6
 */
public class IndexController extends BaseController {

    /**
     * 获取父菜单
     *
     * @param menus
     * @param id
     * @return
     */
    private static Menu getParent(List<Menu> menus, int id) {
        for (Menu m : menus) {
            if (m.getInt("id") == id) {
                return m;
            }
        }
        return null;
    }

    public void captcha() {
        render(new CaptchaRender());
    }

    public void code() {
        setAttr("exp1", "select id UID,login_id CN from users where <%if(user.id != 0){%>  id > ${user.id}<%}%> order by id desc");
        render("/eova/code.html");
    }

    public void toIndex() {
        render("/eova/index.html");
    }

    public void header() {
        User user = getSessionAttr(EovaConst.USER);

        setAttr("user", user);

        render("/eova/header.html");
    }

    public void footer() {
        render("/eova/footer.html");
    }

    public void toIcon() {

        render("/eova/icon.html");
    }

    public void ue() {
        render("/eova/uedemo.html");
    }

    public void form() {
        render("/eova/test/form.html");
    }

    public void toLogin() {
        boolean isCaptcha = xx.toBoolean(EovaConfig.getProps().get("isCaptcha"), true);
        boolean isI18N = xx.toBoolean(EovaConfig.getProps().get("isI18N"), false);
        setAttr("isCaptcha", isCaptcha);
        setAttr("isI18N", isI18N);

        // 应用版权配置
        setAttr("COPYRIGHT", xx.getConfig("app_login_copyright"));

        render("/eova/login.html");
    }

    /**
     * 修改密码
     */
    public void toUpdatePwd() {
        User user = getSessionAttr(EovaConst.USER);
        if (xx.isEmpty(user)) {
            setAttr("msg", "请先登录");
            toLogin();
            return;
        }
        render("/eova/updatePwd.html");
    }

    public void index() {
        /**
         * 用户授权检查(检查为本地License校验,如果屏蔽检查会变成盗版系统,盗版有风险,请使用正版系统)
         *
         * 注意：EOVA 受国家计算机软件著作权保护（登记号：2018SR1012969），不得分享传播源码、二次转售、组团购买等，违者必究。
         * <<计算机软件保护条例>>
         * 第二十四条　未经软件著作权人许可，有下列侵权行为的...依法追究刑事责任：
         * （一）复制或者部分复制著作权人的软件的；
         * （二）向公众发行、出租、通过信息网络传播著作权人的软件的；
         * （三）故意避开或者破坏著作权人为保护其软件著作权而采取的技术措施的；
         * （四）故意删除或者改变软件权利管理电子信息的；
         * （五）转让或者许可他人行使著作权人的软件著作权的。
         * 有前款第一项或者第二项行为的，可以并处每件100元或者货值金额1倍以上5倍以下的罚款；有前款第三项、第四项或者第五项行为的，可以并处20万元以下的罚款。
         *
         * 竭诚为您提供最好的技术服务!
         * 共建良性生态,合作长期共赢!
         * 需要服务请联系:admin@eova.cn
         *
         **/
        String appId = EovaConfig.APP_ID;
        String appSecret = EovaConfig.APP_SECRET;
        if (xx.isEmpty(appId)) {
            renderHtml("启动之前请先配置应用信息：<br>配置文件：/src/main/resources/default/app.config<br>小贴士：应用ID和应用密钥在【用户中心>专业版应用】<br>立即<a href=\"http://www.eova.cn/my\">免费注册获取</a>");
            return;
        }
        if (!EovaAuth.isAuthApp(appId, appSecret)) {
            renderHtml("您使用了一个无效的应用ID或应用密钥，请仔细检查！<br>应用ID和应用密钥在【用户中心>专业版应用】<br>立即<a href=\"http://www.eova.cn/my\">免费注册获取</a>");
            return;
        }


        User user = getSessionAttr(EovaConst.USER);
        // 已经登录
        if (user != null) {
            // 应用域名配置
            setAttr("APP_DOMAIN", xx.getConfig("app_domain"));
            // 默认主页配置
            setAttr("APP_MAIN", xx.getConfig("app_main"));
            setAttr("APP_MAIN_TITLE", xx.getConfig("app_main_title"));
            //			User user = getSessionAttr(EovaConst.USER);

            List<Menu> menus = buildMenu();

            // 登录初始化
            indexInit(this, user, menus);

            Record tree = WidgetUtil.listToTree(WidgetUtil.modelsToRecords(menus), "0", "id", "parent_id", "children");
            setAttr("tree", tree);

            toIndex();
            return;
        }

        // 未登录
        toLogin();
    }

    public void doExit() {
        // 清除登录状态
        removeSessionAttr(EovaConst.USER);
        redirect("/");
    }

    public void doLogin() {
        String loginId = getPara("loginId");
        String loginPwd = getPara("loginPwd");

        boolean isCaptcha = xx.toBoolean(EovaConfig.getProps().get("isCaptcha"), true);
        if (isCaptcha && !super.validateCaptcha("captcha")) {
            setAttr("msg", "验证码错误，请重新输入！");
            keepPara();
            toLogin();
            return;
        }

        String userDs = xx.getConfig("login.user.ds", xx.DS_EOVA);
        String userTable = xx.getConfig("login.user.table", "eova_user");
        String userId = xx.getConfig("login.user.id", "id");
        String userAccount = xx.getConfig("login.user.account", "login_id");
        String userPassword = xx.getConfig("login.user.password", "login_pwd");
        String userRid = xx.getConfig("login.user.rid", "rid");

        Record r = Db.use(userDs).findFirst(String.format("select * from %s where %s = ?", userTable, userAccount), loginId);
        if (r == null) {
            setAttr("msg", "用户名不存在");
            toLogin();
            return;
        }
        if (!r.getStr(userPassword).equals(EncryptUtil.getSM32(loginPwd))) {
            setAttr("msg", "密码错误");
            keepPara("loginId");
            toLogin();
            return;
        }

        User user = new User();
        user.set("id", r.get(userId));
        user.set("rid", r.getInt(userRid));
        user.put(userAccount, r.get(userAccount));

        boolean isI18N = xx.getConfigBool("isI18N", false);
        if (isI18N) {
            String local = getPara(EovaConst.LOCAL);
            if (!xx.isEmpty(local)) {
                // 记录当前语种
                setSessionAttr(EovaConst.LOCAL, local);
                // 加载国际化文案
                List<Record> list = Db.use(xx.DS_EOVA).find("select * from eova_i18n where val is not null or val <> ''");
                I18NBuilder.init(list);
                xx.info("%s语言,加载文案总数:%s", local, list.size());
            }
        }

        try {
            loginInit(this, user, r);
            user.init();
        } catch (Exception e) {
            e.printStackTrace();
            setAttr("msg", e.getMessage());
            keepPara("loginId");
            toLogin();
            return;
        }
        setSessionAttr(EovaConst.USER, user);

        // 重定向到首页
        redirect("/");
    }

    /**
     * 首页初始化
     *
     * @param ctrl
     * @param user  当前用户
     * @param menus 当前菜单
     * @throws Exception
     */
    protected void indexInit(Controller ctrl, User user, List<Menu> menus) {
        String userAccount = xx.getConfig("login.user.account", "login_id");
        setAttr("LOGO_INFO", String.format("%s[%s]", user.role.getStr("name"), user.getStr(userAccount)));
    }

    /**
     * 登录初始化
     *
     * @param ctrl
     * @param user 当前登录用户数据
     * @param e    当前登录帐号数据集
     * @throws Exception
     */
    protected void loginInit(Controller ctrl, User user, Record e) throws Exception {
        // 初始化获取授权信息
        Set<String> auths = new HashSet<String>();
        String sql = "SELECT bs FROM eova_role_btn rf LEFT JOIN eova_button b ON rf.bid = b.id WHERE rf.rid = ?";
        List<Record> bss = Db.use(xx.DS_EOVA).find(sql, user.getRid());
        for (Record r : bss) {
            String bs = r.getStr("bs");
            if (xx.isEmpty(bs)) {
                continue;
            }
            if (!bs.contains(";")) {
                auths.add(bs);
                continue;
            }
            String[] strs = bs.split(";");
            for (String str : strs) {
                auths.add(str);
            }
        }
        if (xx.isEmpty(auths)) {
            throw new Exception("用户角色没有任何授权,请联系管理员授权");
        }
        user.put("auths", auths);

        // 子类可重写添加业务属性和对象
    }

    /**
     * 修改密码
     */
    public void updatePwd() {
        String oldPwd = getPara("oldPwd");
        String newPwd = getPara("newPwd");
        String confirm = getPara("confirm");

        if (xx.isOneEmpty(oldPwd, newPwd, confirm)) {
            renderJson(new Easy("三个密码都不能为空"));
            return;
        }

        // 新密码和确认密码是否一致
        if (!newPwd.equals(confirm)) {
            renderJson(new Easy("新密码两次输入不一致"));
            return;
        }

        String userDs = xx.getConfig("login.user.ds", xx.DS_EOVA);
        String userTable = xx.getConfig("login.user.table", "eova_user");
        String userId = xx.getConfig("login.user.id", "id");
        String userPassword = xx.getConfig("login.user.password", "login_pwd");

        Record r = Db.use(userDs).findFirst(String.format("select %s,%s from %s where %s = ?", userId, userPassword, userTable, userId), getUser().getInt("id"));
        String pwd = r.getStr(userPassword);

        // 旧密码是否正确
        if (!pwd.equals(EncryptUtil.getSM32(oldPwd))) {
            renderJson(new Easy("密码错误"));
            return;
        }

        // 修改密码
        r.set(userPassword, EncryptUtil.getSM32(newPwd));
        Db.use(userDs).update(userTable, r);

        renderJson(new Easy());
    }

    /**
     * 获取菜单JSON
     */
    public void showTree() {
        // 获取父节点
        Integer rootId = getParaToInt(0);
        if (rootId == null) {
            renderJson("系统异常");
            return;
        }
        // 获取登录用户的角色
        User user = getSessionAttr(EovaConst.USER);
        int rid = user.getRid();

        // 获取所有菜单信息
        LinkedHashMap<Integer, Menu> allMenu = (LinkedHashMap<Integer, Menu>) sm.auth.getByParentId(rootId);
        // 格式化EasyUI Tree Data
        WidgetUtil.formatEasyTree(allMenu);
        // 根据角色获取已授权菜单Code
        List<String> authMenuCodeList = sm.auth.queryMenuCodeByRid(rid);

        // 获取已授权菜单
        LinkedHashMap<Integer, Menu> authMenu = new LinkedHashMap<Integer, Menu>();
        for (Map.Entry<Integer, Menu> map : allMenu.entrySet()) {
            Menu menu = map.getValue();
            // TODO 仅可查看已授权部分
            if (authMenuCodeList.contains(menu.getStr("code"))) {
                authMenu.put(map.getKey(), menu);
            }
        }

        // 获取已授权子菜单的所有上级节点(若功能有授权，需要找到上级才能)
        LinkedHashMap<Integer, Menu> authParent = new LinkedHashMap<Integer, Menu>();
        for (Map.Entry<Integer, Menu> map : authMenu.entrySet()) {
            WidgetUtil.getParent(allMenu, authParent, map.getValue());
        }

        // 根节点不显示排除
        authParent.remove(rootId);

        // 将已授权的子菜单 放入 已授权 父菜单 Map
        // 顺序说明：父在前，子在后,子默认又是有序的
        authParent.putAll(authMenu);

        // Map 转 Tree Json
        //		String json = WidgetUtil.menu2TreeJson(authParent, rootId);

        //		renderJson("废弃");
    }

    /**
     * 已授权菜单
     */
    public void menu() {

        // 获取所有菜单
        List<Menu> menus = buildMenu();
        // TODO 多数据库Ztree打开状态兼容
        //		for (Menu m : menus) {
        //			m.put("open", m.getBoolean("is_open"));
        //		}
        Record tree = WidgetUtil.listToTree(WidgetUtil.modelsToRecords(menus), "0", "id", "parent_id", "children");

        renderJson(JsonKit.toJson(tree.get("children")));
    }

    /**
     * 构建当前用户可用菜单数据集
     *
     * @return
     */
    private List<Menu> buildMenu() {
        User user = getSessionAttr(EovaConst.USER);

        // 获取所有菜单
        List<Menu> menus = Menu.dao.queryMenu();
        // 获取已授权菜单ID
        List<Integer> ids = Button.dao.queryMenuIdByRid(user.getRid());

        // 递归查找已授权功能的上级节点
        HashSet<Integer> authPid = new HashSet<Integer>();
        for (Integer id : ids) {
            Menu m = getParent(menus, id);
            findParent(authPid, menus, m);
        }

        Iterator<Menu> it = menus.iterator();
        while (it.hasNext()) {
            Menu m = it.next();
            m.put("link", m.getUrl());
            m.remove("url");

            Integer mid = m.getInt("id");

            // 已授权目录
            if (xx.isContains(authPid, mid))
                continue;

            if (!xx.isContains(ids, mid)) {
                it.remove();
            }
        }

        I18NBuilder.models(menus, "name");
        return menus;
    }

    /**
     * 初始化操作
     */
    public void init() {
        render("/eova/init.html");
    }

    /**
     * 升级操作
     */
    public void upgrade() {
        String isUpgrade = EovaConfig.getProps().get("isUpgrade");
        if (xx.isEmpty(isUpgrade) || !isUpgrade.equals("true")) {
            renderText("未开启升级模式，请启动配置 isUpgrade = true, 用完之后立马关掉,后果自负!");
            return;
        }

        render("/eova/help/upgrade.html");
    }

    /**
     * 初始化操作
     */
    public void doInit() {
        String ip = getPara("ip");
        String port = getPara("port");
        String userName = getPara("userName");
        String password = getPara("password");

        keepPara(ip);
        keepPara(port);
        keepPara(userName);
        keepPara(password);

        String local_url = MessageFormat.format("jdbc:mysql://{0}:{1}/web?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull", ip, port);
        // String local_user = userName;
        // String local_pwd = password;

        // String local_eova_url = MessageFormat.format("jdbc:mysql://{0}:{1}/eova?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull", ip, port);
        // String local_eova_user = userName;
        // String local_eova_pwd = password;

        // 自动生成JDBC配置 to eova.config
        String msg = JdbcUtil.initConnection(local_url, userName, password);
        if (msg != null) {

            if (msg.startsWith("Communications link failure")) {
                msg = "无法连接数据库，请检查IP:Port";
            } else if (msg.startsWith("Access denied for user")) {
                msg = "用户无权限访问，请检查用户名和密码";
            }

            setAttr("msg", msg);
            init();
            return;
        }

    }

    /**
     * 递归向上查找父节点
     *
     * @param authPid 找到的父节点
     * @param menus   所有菜单
     * @param m
     */
    private void findParent(HashSet<Integer> authPid, List<Menu> menus, Menu m) {
        if (m == null) {
            return;
        }
        Integer pid = m.getInt("parent_id");
        if (pid == 0) {
            return;
        }
        authPid.add(pid);

        Menu p = getParent(menus, pid);
        findParent(authPid, menus, p);
    }
}