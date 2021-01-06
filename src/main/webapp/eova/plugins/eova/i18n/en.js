$.eova = {
	i18n : {
		
		您要隐藏当前所选数据 : 'do you want to hide the currently selected data?',
		您要删除当前所选数据 : 'do you want to delete the currently selected data?',
		请勿选择多行数据 : 'do not select multi-row data!',
		请选择一行数据 : 'please select one row of data!',
		您确认执行当前操作 : 'You confirm that the current operation is performed?',
		
		服务异常 : 'service exception, please try again later!',
		操作提示 : 'operating hints',
		隐藏成功 : 'hide success!',
		删除成功 : 'delete successful!',
		操作成功 : 'operation is successful',
		清空查询 : 'clear query',
		
		新增成功 : 'Add {0} succeed',
		修改成功 : 'Update {0} succeed',
		
		请输入信息 : 'Please enter the info',
		请确认 : 'Please confirm',
		查询 : 'Query',
		确定 : 'OK',
		保存 : 'Save',
		取消 : 'Cancel',
		提示 : 'Prompt',
		警告 : 'Warning',
		错误 : 'Error',
		
		最大化 : 'Maximize',
		还原 : 'Restore',
		刷新 : 'Refresh',

		导出当前查询数据 : 'Export the current query data',
		导出本页数据 : 'Export this page data',
		删除行 : 'Delete Row',
		新增行 : 'Add Row',
		保存数据 : 'Save Row',
		
		清空选择 : 'Clear Select',
		请选择数据 : 'Please Select',
		
	}
}

if ($.fn.pagination) {
	$.fn.pagination.defaults.beforePageText = 'Page';
	$.fn.pagination.defaults.afterPageText = 'of {pages}';
	$.fn.pagination.defaults.displayMsg = 'Displaying {from} to {to} of {total} items';
}
if ($.fn.datagrid) {
	$.fn.datagrid.defaults.loadMsg = 'Processing, please wait ...';
}
if ($.fn.treegrid && $.fn.datagrid) {
	$.fn.treegrid.defaults.loadMsg = $.fn.datagrid.defaults.loadMsg;
}
if ($.messager) {
	$.messager.defaults.ok = 'OK';
	$.messager.defaults.cancel = 'Cancel';
}
if ($.fn.validatebox) {
	$.fn.validatebox.defaults.missingMessage = 'This field is required.';
	$.fn.validatebox.defaults.rules.email.message = 'Please enter a valid email address.';
	$.fn.validatebox.defaults.rules.url.message = 'Please enter a valid URL.';
	$.fn.validatebox.defaults.rules.length.message = 'Please enter a value between {0} and {1}.';
	$.fn.validatebox.defaults.rules.remote.message = 'Please fix this field.';
}
if ($.fn.numberbox) {
	$.fn.numberbox.defaults.missingMessage = 'This field is required.';
}
if ($.fn.combobox) {
	$.fn.combobox.defaults.missingMessage = 'This field is required.';
}
if ($.fn.combotree) {
	$.fn.combotree.defaults.missingMessage = 'This field is required.';
}
if ($.fn.combogrid) {
	$.fn.combogrid.defaults.missingMessage = 'This field is required.';
}
if ($.fn.calendar) {
	$.fn.calendar.defaults.weeks = [ 'S', 'M', 'T', 'W', 'T', 'F', 'S' ];
	$.fn.calendar.defaults.months = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ];
}
if ($.fn.datebox) {
	$.fn.datebox.defaults.currentText = 'Today';
	$.fn.datebox.defaults.closeText = 'Close';
	$.fn.datebox.defaults.okText = 'Ok';
	$.fn.datebox.defaults.missingMessage = 'This field is required.';
}
if ($.fn.datetimebox && $.fn.datebox) {
	$.extend($.fn.datetimebox.defaults, {
		currentText : $.fn.datebox.defaults.currentText,
		closeText : $.fn.datebox.defaults.closeText,
		okText : $.fn.datebox.defaults.okText,
		missingMessage : $.fn.datebox.defaults.missingMessage
	});
}