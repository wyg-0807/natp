// 确认删除
var confirmDel = function(ID, code) {
	tableConfirm(ID, $.I18N('请确认'), $.I18N('您要删除当前所选数据'), $.str.format('/grid/delete/{0}', code), $.I18N('删除成功'));
}

// 确认隐藏
var confirmHide = function(ID, code) {
	tableConfirm(ID, $.I18N('请确认'), $.I18N('您要隐藏当前所选数据'), $.str.format('/grid/hide/{0}', code), $.I18N('隐藏成功'));
}

// 弹出Dialog
var dialog = function(ID, name, url, width, height) {
	$.dialog(ID, name, url, width, height);
}

// 有且只有一行数据被选中
function isSelected(ID) {
	var num = getSelectRows(ID).length;
	if (num == 0) {
		$.msg($.I18N('请选择一行数据'));
		return false;
	}
	if (num > 1) {
		$.msg($.I18N('请勿选择多行数据'));
		return false;
	}
	return true;
}

// 获取选中行字段值
function getSelectVal(ID, fieldName) {
	if (!fieldName)
		$.msg('获取选中值,字段名不能为空');
	var row = getSelectRow(ID);
	if(row)
		return row[fieldName];
	return undefined;
}

// 获取选中行数据
function getSelectRow(ID) {
	if(isSelected(ID)){
		return getSelectRows(ID)[0];
	}
}

// 获取所有选中行数据
function getSelectRows(ID) {
	var rows;
	layui.use('table', function() {
		rows = layui.table.checkStatus(ID).data;
	});
	return rows;
}

// 获取所有选中行的ID集合字符串逗号拼接
function getSelectIds(ID){
	var rows = getSelectRows(ID);
	var vals = [];
    for (var i = 0; i < rows.length; i++) { //组成一个字符串，ID主键用逗号隔开
    	vals.push(rows[i]['id']);
    }
	return vals.join(',');
}

// 表格二次确认操作
function tableConfirm(ID, title, info, url, msg) {
	layui.use([ 'table' ], function() {
		var $ = layui.$, table = layui.table;
		var rows = getSelectRows(ID);
		if(!rows || rows.length == 0){
			$.msg($.I18N('请选择一行数据'));
			return;
		}

		layer.confirm(info, {
			title : title,
			icon : 3,
			btn : [ '确定', '取消' ]
		}, function() {
			$.syncPost(url, {
				rows : JSON.stringify(rows)
			}, function(result, status) {
				if (result.success) {
					$.msg(msg);
					layer.close(layer.getFrameIndex(window.name));

					table.reload(ID);
				} else {
					$.alert(result.msg);
					layer.close(layer.getFrameIndex(window.name));
				}
			});
		});
	});
}