package com.baidu.ueditor;

import com.eova.common.base.BaseController;
import com.jfinal.aop.Clear;

/**
 * @author lzhq
 */
@Clear
public class Dispatcher extends BaseController {

    public void dispatch() {
        String res = new ActionEnter(getRequest(), getRequest().getServletContext().getRealPath("/")).exec();
        renderText(res);
    }

}
