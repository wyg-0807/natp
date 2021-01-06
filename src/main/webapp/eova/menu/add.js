function typeChange(oldValue, newValue) {
	if (newValue == 'dir') {
		$.msg('友情提示:空目录不显示,请保证目录下有功能哦');
	}
	
	// diy 需要显示 自定义URL字段,并启用校验
	if (newValue == 'diy') {
		$('#trUrl').show();
		$form.validator("setField", "url", "自定义业务URL:required;");
	} else {
		if (oldValue == 'diy') {
			$form.validator("setField", "url", null);
			$('#trUrl').hide();
		}
	}
	// office
	if (newValue == 'office') {
		$('#office').show();
		$form.validator("setField", "path", "Office模版路径:required;");
	} else {
		if (oldValue == 'office') {
			$form.validator("setField", "path", null);
			$('#office').hide();
		}
	}

	$('#template').hide();

	if (newValue == 'single_grid') {
		$form.validator('setField', {
			objectCode : '元对象:required;'
		});
		$('#templatename').text('[单表]');
		$('#templateimg').html('<img src="/eova/ui/images/template/single_grid.png" >');
		$('#template').show();
	} else {
		if (oldValue == 'single_grid') {
			$form.validator('setField', {
				objectCode : null
			});
		}
	}

	if (newValue == 'single_tree') {
		$form.validator('setField', {
			rootPid : '根节点父ID:required;',
			singleTreeObjectCode : '元对象:required;',
			treeField : '树形字段:required;',
			parentField : '关联字段:required;'
		});
		$('#templatename').text('[单表树]');
		$('#templateimg').html('<img src="/eova/ui/images/template/single_tree.png" >');
		$('#template').show();
	} else {
		if (oldValue == 'single_tree') {
			$form.validator('setField', {
				rootPid : null,
				singleTreeObjectCode : null,
				treeField : null,
				parentField : null
			});
		}
	}

	if (newValue == 'single_chart') {
		$form.validator('setField', {
			singleChartType : '图类型:required;',
			singleChartObjectCode : '元对象:required;',
			singleChartYunit : 'Y轴单位:required;',
			singleChartX : 'X轴字段:required;',
			singleChartY : 'Y轴字段:required;'
		});
		$('#templatename').text('[单表图]');
		$('#templateimg').html('<img src="/eova/ui/images/template/single_chart.png" >');
		$('#template').show();
	} else {
		if (oldValue == 'single_chart') {
			$form.validator('setField', {
				singleChartObjectCode : null,
				singleChartYunit : null,
				singleChartX : null,
				singleChartY : null
			});
		}
	}

	if (newValue == 'master_slave_grid') {
		$form.validator('setField', {
			masterObjectCode : '主对象:required;',
			slaveObjectCode : '子对象:required;',
			masterFieldCode : '主外键字段:required;',
			slaveFieldCode : '子关联字段:required;'
		});
		$('#templatename').text('[主子表]');
		$('#templateimg').html('<img src="/eova/ui/images/template/master_slave_grid.png" >');
		$('#template').show();
	} else {
		if (oldValue == 'master_slave_grid') {
			$form.validator('setField', {
				masterObjectCode : null,
				slaveObjectCode : null,
				masterFieldCode : null,
				slaveFieldCode : null
			});
		}
	}

	if (newValue == 'tree_grid') {
		$form.validator('setField', {
			treeGridTreeObjectCode : '树元对象:required;',
			treeGridTreeFieldCode : '树关联字段:required;',

			treeGridRootPid : '根节点父ID:required;',
			treeGridParentField : 'PID字段:required;',
			treeGridTreeField : '树形字段:required;',

			treeGridObjectCode : 'Grid元对象:required;',
			treeGridFieldCode : 'Grid外键字段:required;'
		});
		$('#templatename').text('[树&表]');
		$('#templateimg').html('<img src="/eova/ui/images/template/tree_grid.png" >');
		$('#template').show();
	} else {
		if (oldValue == 'tree_grid') {
			$form.validator('setField', {
				treeGridTreeObjectCode : null,
				treeGridTreeFieldCode : null,

				treeGridRootPid : null,
				treeGridParentField : null,
				treeGridTreeField : null,

				treeGridObjectCode : null,
				treeGridFieldCode : null,
			});
		}
	}

	$('#' + newValue).show();
	$('#' + oldValue).hide();
}

$(function() {

	var $type = $('#type').eovacombo({
		json : [
		        {id:'dir', cn : '父目录菜单'},
		        {id:'diy', cn : '自定义业务'},
		        {id:'single_grid', cn : '单表'},
		        {id:'single_tree', cn : '单表树'},
		        {id:'single_chart', cn : '单表图'},
		        {id:'master_slave_grid', cn : '主子表'},
		        {id:'tree_grid', cn : '树&表'},
		        {id:'office', cn : 'Office'}
		],
		onChange: function (oldValue, newValue) {
			typeChange(oldValue, newValue);
	    }
	}).reload();
	
	var $office_type = $('#office_type').eovacombo({
		json : [
		        {id:'xls', cn : 'Excel'},
		        {id:'doc', cn : 'Word'},
		        {id:'pdf', cn : 'PDF暂不支持'}
		        ]
	}).reload();
	
	// --------------------------------------------------单表树 级联
	var $single_tree_object = $('#single_tree_object');
	var $single_tree_order = $('#single_tree_order');
	var $single_tree_icon = $('#single_tree_icon');
	var $single_tree_field = $('#single_tree_field');
	var $single_tree_parent = $('#single_tree_parent');
	var $single_tree_id = $('#single_tree_id');

	// 初始化禁用
	$single_tree_order.eovafind().readonly(true);
	$single_tree_icon.eovafind().readonly(true);
	$single_tree_field.eovafind().readonly(true);
	$single_tree_parent.eovafind().readonly(true);
	$single_tree_id.eovafind().readonly(true);

	$single_tree_object.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$single_tree_icon.eovafind().readonly(true);
				$single_tree_field.eovafind().readonly(true);
				$single_tree_parent.eovafind().readonly(true);
				$single_tree_id.eovafind().readonly(true);
				return;
			}

			$single_tree_order.eovafind().readonly(false);
			$single_tree_order.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_tree_icon.eovafind().readonly(false);
			$single_tree_icon.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_tree_icon.eovafind().readonly(false);

			$single_tree_field.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_tree_field.eovafind().readonly(false);

			$single_tree_parent.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_tree_parent.eovafind().readonly(false);

			$single_tree_id.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_tree_id.eovafind().readonly(false);
		}
	});

	// --------------------------------------------------单表图 级联
	var $single_chart_object = $('#single_chart_object');
	var $single_chart_x = $('#single_chart_x');
	var $single_chart_y = $('#single_chart_y');
	
	var $single_chart_type = $('#single_chart_type').eovacombo({
		json : [
		        {id:'1', cn : '折线图'},
		        {id:'2', cn : '柱状图'}
		        ]
	}).reload();

	// 初始化禁用
	$single_chart_x.eovafind().readonly(true);
	$single_chart_y.eovafind().readonly(true);

	$single_chart_object.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$single_chart_x.eovafind().readonly(true);
				$single_chart_y.eovafind().readonly(true);
				return;
			}

			$single_chart_x.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_chart_x.eovafind().readonly(false);

			$single_chart_y.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$single_chart_y.eovafind().readonly(false);
		}
	});

	// --------------------------------------------------主子表级联

	var $masterObjectCode = $('#masterObjectCode');
	var $masterFieldCode = $('#masterFieldCode');

	var $slaveObjectCode1 = $('#slaveObjectCode1');
	var $slaveFieldCode1 = $('#slaveFieldCode1');

	var $slaveObjectCode2 = $('#slaveObjectCode2');
	var $slaveFieldCode2 = $('#slaveFieldCode2');

	var $slaveObjectCode3 = $('#slaveObjectCode3');
	var $slaveFieldCode3 = $('#slaveFieldCode3');

	var $slaveObjectCode4 = $('#slaveObjectCode4');
	var $slaveFieldCode4 = $('#slaveFieldCode4');

	var $slaveObjectCode5 = $('#slaveObjectCode5');
	var $slaveFieldCode5 = $('#slaveFieldCode5');

	// 初始化禁用
	$masterFieldCode.eovafind().readonly(true);
	$slaveFieldCode1.eovafind().readonly(true);
	$slaveFieldCode2.eovafind().readonly(true);
	$slaveFieldCode3.eovafind().readonly(true);
	$slaveFieldCode4.eovafind().readonly(true);
	$slaveFieldCode5.eovafind().readonly(true);

	$masterObjectCode.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$masterFieldCode.eovafind().readonly(true);
				return;
			}

			$masterFieldCode.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$masterFieldCode.eovafind().readonly(false);
		}
	});

	$slaveObjectCode1.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$slaveFieldCode1.eovafind().readonly(true);
				return;
			}

			$slaveFieldCode1.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$slaveFieldCode1.eovafind().readonly(false);
		}
	});
	$slaveObjectCode2.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$slaveFieldCode2.eovafind().readonly(true);
				return;
			}

			$slaveFieldCode2.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$slaveFieldCode2.eovafind().readonly(false);
		}
	});
	$slaveObjectCode3.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$slaveFieldCode3.eovafind().readonly(true);
				return;
			}

			$slaveFieldCode3.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$slaveFieldCode3.eovafind().readonly(false);
		}
	});
	$slaveObjectCode4.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$slaveFieldCode4.eovafind().readonly(true);
				return;
			}

			$slaveFieldCode4.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$slaveFieldCode4.eovafind().readonly(false);
		}
	});
	$slaveObjectCode5.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$slaveFieldCode5.eovafind().readonly(true);
				return;
			}

			$slaveFieldCode5.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$slaveFieldCode5.eovafind().readonly(false);
		}
	});

	// --------------------------------------------------树&表 级联

	var $tree_grid_tree_object = $('#tree_grid_tree_object');
	var $tree_grid_tree_field = $('#tree_grid_tree_field');

	var $tree_grid_parent = $('#tree_grid_parent');
	var $tree_grid_id = $('#tree_grid_id');
	var $tree_grid_tree = $('#tree_grid_tree');
	var $tree_grid_icon = $('#tree_grid_icon');

	var $tree_grid_object_code = $('#tree_grid_object_code');
	var $tree_grid_field_code = $('#tree_grid_field_code');

	// 初始化禁用
	$tree_grid_tree_field.eovafind().readonly(true);

	$tree_grid_parent.eovafind().readonly(true);
	$tree_grid_id.eovafind().readonly(true);
	$tree_grid_tree.eovafind().readonly(true);
	$tree_grid_icon.eovafind().readonly(true);

	$tree_grid_field_code.eovafind().readonly(true);
	// Tree字段级联
	$tree_grid_tree_object.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$tree_grid_parent.eovafind().readonly(true);
				$tree_grid_id.eovafind().readonly(true);
				$tree_grid_tree.eovafind().readonly(true);
				$tree_grid_icon.eovafind().readonly(true);
				return;
			}

			$tree_grid_tree_field.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$tree_grid_tree_field.eovafind().readonly(false);

			$tree_grid_parent.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$tree_grid_parent.eovafind().readonly(false);

			$tree_grid_id.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$tree_grid_id.eovafind().readonly(false);

			$tree_grid_tree.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$tree_grid_tree.eovafind().readonly(false);

			$tree_grid_icon.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$tree_grid_icon.eovafind().readonly(false);
		}
	});
	// Grid字段级联
	$tree_grid_object_code.eovafind({
		onChange : function(oldValue, newValue) {
			if (newValue == '') {
				$tree_grid_field_code.eovafind().readonly(true);
				return;
			}

			$tree_grid_field_code.eovafind({
				exp : 'selectEovaFieldByObjectCode,' + newValue
			});
			$tree_grid_field_code.eovafind().readonly(false);
		}
	});

});