$.eova = {
	i18n : {
		
		您要隐藏当前所选数据 : '您要隐藏当前所选数据？',
		您要删除当前所选数据 : '您要删除当前所选数据？',
		请勿选择多行数据 : '请勿选择多行数据！',
		请选择一行数据 : '请选择一行数据',
		请选择一个节点 : '请选择一个节点',
		请勾选一个节点 : '请勾选一个节点',
		您确认执行当前操作 : '您确认执行当前操作？',

		服务异常 : '服务异常，请稍后再试',
		操作提示 : '操作提示',
		隐藏成功 : '隐藏成功',
		删除成功 : '删除成功',
		操作成功 : '操作成功',
		清空查询 : '清空查询',

		新增成功 : '新增{0}成功',
		修改成功 : '修改{0}成功',
		
		请输入 : '请输入',
		请确认 : '请确认',
		查询 : '快速查询',
		确定 : '确定',
		保存 : '保 存',
		取消 : '取 消',
		提示 : '提示',
		警告 : '警告',
		错误 : '错误',

	}
}

if ($.fn.pagination) {
	$.fn.pagination.defaults.beforePageText = '第';
	$.fn.pagination.defaults.afterPageText = '共{pages}页';
	$.fn.pagination.defaults.displayMsg = '显示{from}到{to},共{total}记录';
}
if ($.fn.datagrid) {
	$.fn.datagrid.defaults.loadMsg = '正在处理，请稍待。。。';
}
if ($.fn.treegrid && $.fn.datagrid) {
	$.fn.treegrid.defaults.loadMsg = $.fn.datagrid.defaults.loadMsg;
}
if ($.messager) {
	$.messager.defaults.ok = '确定';
	$.messager.defaults.cancel = '取消';
}
if ($.fn.validatebox) {
	$.fn.validatebox.defaults.missingMessage = '该输入项为必输项';
	$.fn.validatebox.defaults.rules.email.message = '请输入有效的电子邮件地址';
	$.fn.validatebox.defaults.rules.url.message = '请输入有效的URL地址';
	$.fn.validatebox.defaults.rules.length.message = '输入内容长度必须介于{0}和{1}之间';
	$.fn.validatebox.defaults.rules.remote.message = '请修正该字段';
}
if ($.fn.numberbox) {
	$.fn.numberbox.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.combobox) {
	$.fn.combobox.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.combotree) {
	$.fn.combotree.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.combogrid) {
	$.fn.combogrid.defaults.missingMessage = '该输入项为必输项';
}
if ($.fn.calendar) {
	$.fn.calendar.defaults.weeks = [ '日', '一', '二', '三', '四', '五', '六' ];
	$.fn.calendar.defaults.months = [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ];
}
if ($.fn.datebox) {
	$.fn.datebox.defaults.currentText = '今天';
	$.fn.datebox.defaults.closeText = '关闭';
	$.fn.datebox.defaults.okText = '确定';
	$.fn.datebox.defaults.missingMessage = '该输入项为必输项';
	$.fn.datebox.defaults.formatter = function(date) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
	};
	$.fn.datebox.defaults.parser = function(s) {
		if (!s)
			return new Date();
		var ss = s.split('-');
		var y = parseInt(ss[0], 10);
		var m = parseInt(ss[1], 10);
		var d = parseInt(ss[2], 10);
		if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
			return new Date(y, m - 1, d);
		} else {
			return new Date();
		}
	};
}
if ($.fn.datetimebox && $.fn.datebox) {
	$.extend($.fn.datetimebox.defaults, {
		currentText : $.fn.datebox.defaults.currentText,
		closeText : $.fn.datebox.defaults.closeText,
		okText : $.fn.datebox.defaults.okText,
		missingMessage : $.fn.datebox.defaults.missingMessage
	});
}