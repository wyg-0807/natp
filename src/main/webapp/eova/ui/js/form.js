
function submitForm($$, ID, $form, action, msg) {
	submitForm($$, ID, $form, action, msg, undefined);
};

/**
 * 提交表单给服务器处理
 * @param {*} $$ 当前窗口上层JQuery.$
 * @param {*} ID 组件 ID
 * @param {*} $form 当前提交表单
 * @param {*} action 提交后服务端处理URL
 * @param {*} msg 处理成功提示信息
 * @param {*} data 自定义业务数据
 */
function submitForm($$, ID, $form, action, msg, data) {
	// nice validator 校验通过就提交表单
	$form.isValid(function(isValied) {
		if (isValied) {
			var o = $.getFormParasObj($form);
			if(data){
				// 特殊命名防止与正常业务重名
				o['eova_data'] = $.json.toStr(data);
			}
			$.post(action, o, function (result) {
				if (result.success) {
					$$.msg(msg);
					if(typeof ID === 'function') {
						// 复杂场景自定义刷新 You Can You Up
						ID();
						// 既然如此以后有时间,应该优化为,确定成功回调函数,并且返回result,可以做更多.
					} else {
						// Form模式基本都是更新父页面的组件
						$.parentTableReLoad(ID);
					}
					$.dialogClose(parent.layer, window);
				} else {
					$.error(result.msg);
				}
			}, 'json');
		}
	});
};