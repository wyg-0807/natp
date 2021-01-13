(function ($) {
	var SP = "★";
	$.table = {
		/**
		 * 表格右上侧自定义Toolbar 配置
		 */
		diyToolbar: [{
			isAdmin: true,
			title: '元字段编辑',
			layEvent: 'TABLE_FIELDS',
			icon: 'layui-icon-util'
		}, {
			isAdmin: true,
			title: '元对象编辑',
			layEvent: 'TABLE_OBJECT',
			icon: 'layui-icon-circle-dot'
		}, {
			isAdmin: true,
			title: '保存列宽',
			layEvent: 'SAVE_WIDTH',
			icon: 'eova-icon-table'
		}, {
			isAdmin: false,
			title: '导出所有查询数据',
			layEvent: 'EOVA_EXPORT_QUERY',
			icon: 'layui-icon-export'
		}],
		buildDiyToolbar: function(isAdmin){
			var t = [];
		    $.each($.table.diyToolbar, function(i, o){
		    	if(!o.isAdmin){
		    		t.push(o);
		    	} else {
		    		if(isAdmin) t.push(o);
		    	}
		    })
		    return t;
		},
		/**
		 * 表格右上侧自定义Toolbar 事件
		 * @param {*} obj 按钮
		 */
		diyToolbarEvent: function (obj, object, menuCode, ID, table) {
			switch (obj.event) {
				case 'TABLE_FIELDS':
					window.open('/meta/edit/' + object.code);
					break;
				case 'TABLE_OBJECT':
					var url = '/form/update/eova_object_code-' + object.id;
					dialog(ID, '元对象编辑' + object.code, url, 720, 550);
					break;
				case 'SAVE_WIDTH':
					var $header = table.getHeader(ID);
					var widths = [];
					$.each($header.find('th'), function (i, o) {
						var $o = $(o);
						if ($o.data('field') != "0") {
							widths.push(parseInt($o.width()));
						}
					});
					$.getJSON('/grid/updateWidths/' + object.code + '-' + widths.join(','), function () {
						$.msg("当前表格宽度已保存");
					});
					break;
				case 'EOVA_EXPORT_QUERY':
					var url = '/grid/export/' + object.code;
					if (menuCode != '') {
						url = url + '-' + menuCode;
					}

					// URL参数
					var urlParas = $.getUrlParas();
					if (urlParas) {
						url = url + '?' + urlParas;
					}

					// 构建Form表单模拟Post提交
					var $form = $($.str.format("<form method='post' action='{0}' target='_blank'></form>", url));
					$.each($('#queryForm').serializeArray(), function (i, o) {
						$form.append($.str.format("<input type='hidden' name='{0}' value='{1}'/>", o.name, o.value));
					});
					$(document.body).append($form);
					$form.submit();
					break;
			};
		},
		/**
		 * 构建列
		 * 
		 * @param {*} object 元对象
		 * @param {*} cols 列集合
		 */
		buildFirstColumn: function (object, fields, cols) {
			var first = new Object;
			first.fixed = 'left';
			first.type = 'checkbox';
			if (object.is_single) {
				// 单选显示行号
				first.type = 'numbers';
				// normal（常规列，无需设定）
				// checkbox（复选框列）
				// radio（单选框列，layui 2.4.0 新增）
				// numbers（序号列）
				// space（空列）
			}
			cols.push(first);
		},
		/**
		 * 构建列
		 * 
		 * @param {*} object 元对象
		 * @param {*} fields 元字段
		 * @param {*} cols 列集合
		 */
		buildColumn: function (object, fields, cols) {
			var num = 1;// 第几个显示行
			$.each(fields, function (i, f) {
				if (!f.is_show) {
					// continue;
					return true;
				}

				var col = new Object;

				// TODO 临时增加所有EOVA 元对象 宽度宽 + easyui->layui过渡,V3.x还是在DB中修改
				if (object.code.indexOf('eova') != -1) {
					f.width = f.width + 15;
				}
				col.field = f.en;
				col.title = f.cn;
				col.width = (f.width ? f.width : 150)

				if(f.config){
					var conf = $.json.toObj(f.config);
					if('totalRowText' in conf){
						col.totalRowText = conf.totalRowText;
					} else {
						col.totalRow = conf.totalRow;
					}
				}

				if (f.is_order) {
					col.sort = true;
				}

				// 单元格编辑模式单独增加列宽,用于 padding: 5px
				if (object.is_celledit && f.is_edit) {
					col.width = col.width + 10;
					// 禁用排序,避免格式化被重新渲染
					col.sort = false;
					// 禁止拖拽列宽
					col.unresize = true;
				}

				// 列的格式化处理
				$.table.formatColumn(object, f, col);

				cols.push(col);
				
				num++;
			});
		},
		/**
		 * 构建手工模式行初始化对象模型(格式化会自动根据此对象渲染UI)
		 * 
		 * @param {*} tableIns 表格
		 */
		buildEditRow: function (tableIns, fields) {
			var row = new Object;
			$.each(fields, function (i, f) {
				// 单元格编辑模式
				if (f.is_show && f.is_edit) {
					var fieldName = f.en;
					if (f.type == '下拉框' || f.type == '查找框') {
						// 下拉和查找 有两种值, text 和 value, 行编辑模式需要给值域字段进行初始赋值,因为格式化取的是值域
						fieldName = fieldName + '_val';
					}
					row[fieldName] = '';
				}
			});
			// 把准备好的对象存放在表格实例的配置中,方便外界取用
			tableIns.config.editrow = row;
		},
		/**
		 * 格式化列
		 * 
		 * @param {*} object 元对象
		 * @param {*} f 元字段
		 * @param {*} col 当前列
		 */
		formatColumn: function (object, f, col) {
			
			function getKey(row){
				return f.en + SP + (row.pk_val || row.LAY_INDEX);// 主键 || 行索引
			}
			
			if (object.is_celledit && f.is_edit) {
				// 单元格格式化的处理
				if (f.type == '布尔框') {
					col.align = 'center';
					col.templet = function (val, row, index, field) {
						var key = getKey(row);
						return $.str.format('<div class="eova-bool" id="{0}" name="{1}" value="{2}"></div>', key, key, val);
					};
				} else if (f.type == '下拉框') {
					col.templet = function (vlaue, row, index, field) {
						var key = getKey(row);
						var val = row[f.en + '_val'];

						var url = '/widget/comboJson/' + object.code + '-' + field;
						var options = $.str.format("width: {0},url: '{1}',valueField: '{2}', textField: '{3}', multiple: {4}", f.width, url, 'id', 'cn', f.is_multiple);

						return $.str.format('<div class="eova-form-field"><div class="eova-combo" id="{0}" name="{1}" value="{2}" data-options="{3}"></div></div>', key, key, val, options)
					};
				} else if (f.type == '查找框') {
					col.templet = function (value, row, index, field) {
						var key = getKey(row);
						var val = row[f.en + '_val'];

						var options = $.str.format("width: {0},multiple: '{1}'", f.width, f.is_multiple);

						return $.str.format('<div class="eova-form-field"><div class="eova-find" id="{0}" name="{1}" value="{2}" code="{3}" field="{4}" data-options="{5}"></div></div>',
							key, key, val, object.code, f.en, options)
					};
				} else if (f.type == '日期框' || f.type == '时间框') {
					col.templet = function (val, row, index, field) {
						var key = getKey(row);
						var options = $.str.format("width: {0},format: '{1}'", f.width, f.type == '时间框' ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd');
						return $.str.format('<div class="eova-form-field"><div class="eova-time" id="{0}" name="{1}" value="{2}" data-options="{3}"></div></div>', key, key, val, options)
					};
				} else if (f.type == '文本域' || f.type == '编辑框') {
					col.templet = function (val, row, index, field) {
						var key = getKey(row);
						if (val == undefined)
							val = '';
						// 特殊处理,有JS函数,会破坏DOM || JSON 数据丢失
						if (f.en == 'formatter' || f.en == 'config') {
							val = $.str.encodeUrl(val);
						}
						return $.str.format('<div class="eova-form-field"><div class="eova-info" id="{0}" name="{1}" value="{2}" data-options="width: {3}"></div></div>', key, key, val,
							f.width)
					};
				}
				// 默认文本框
				else {
					col.templet = function (value, row, index, field) {
						var key = getKey(row);
						var val = row[f.en];
						if (val == undefined)
							val = '';
						return $.str.format('<div class="eova-form-field"><div class="eova-text" id="{0}" name="{1}" value="{2}" data-options="width: {3}, isBtn: false"></div></div>',
							key, key, val, f.width)
					};
				}
			} else {
				// 常规格式化处理
				if (f.formatter != null && f.formatter != '') {
					col.templet = new Function('return ' + f.formatter + ';')();
				} else {
					// 默认格式化处理
					if (f.type == '布尔框') {
						col.align = 'center';
						col.templet = function (value, row, index, field) {
							var ck = '<i class="eova-icon">&#xe63f;</i>';
							if (value) {
								ck = '<i class="eova-icon">&#x1005;</i>';
							}
							return ck;
						};
					}
					// if (f.type == '文本框' || f.type == '编辑框' || f.type == '文本域') {
					// col.formatter = function (row) {
					// if (value && value.length > 10) {
					// //alert($.encodeHtml(value));
					// return '<span title="' + $.encodeHtml(value) + '">' + $.encodeHtml(value) + '</span>'
					// }
					// return value;
					// }
					// }
				}
			}
		},
		// 单元格字段值改变事件 event(值, 字段名, 所在行索引值)
		change: function (eova, event) {
			$(eova + 'bool').eovabool({
				onChange: function (val) {
					var name = $(this).children().attr('name');
					var index = $(this).parents('tr').data('index');
					event(val, name, index);
				}
			});
			$(eova + 'text').eovatext({
				onChange: function (val) {
					var name = $(this).children().attr('name');
					var index = $(this).parents('tr').data('index');
					event(val, name, index);
				}
			});
			$(eova + 'combo').eovacombo({
				onChange: function (oldVal, newVal) {
					var name = $(this).children().attr('name');
					var index = $(this).parents('tr').data('index');
					event(newVal, name, index);
				}
			});
			$(eova + 'find').eovafind({
				onChange: function (oldVal, newVal) {
					var name = $(this).children().attr('name');
					var index = $(this).parents('tr').data('index');
					event(newVal, name, index);
				}
			});
			$(eova + 'info').eovainfo({
				onChange: function (oldVal, newVal) {
					var name = $(this).children().attr('name');
					var index = $(this).parents('tr').data('index');
					event(newVal, name, index);
				}
			});
			$(eova + 'time').eovatime({
				onChange: function (oldVal, newVal) {
					var name = $(this).children().attr('name');
					var index = $(this).parents('tr').data('index');
					event(newVal, name, index);
				}
			});

		},
		// 单元格数据处理
		process: function (ID, code) {
			// Table 单元格内 Eova控件 值变化 事件绑定
			var eova = 'div[lay-id="' + ID + '"] .layui-table-body .eova-';
			
			var to;
			$.table.change(eova, function event(val, name, index) {
				if (code != '') {
					// 以最后的保存事件为准
					clearTimeout(to);
					to = setTimeout(function () {
						$.table.save(ID, code, val, name);
				    }, 500);
				} else {
					$.table.update(ID, index, val, name);
				}
			});
		},
		// 保存在线数据
		save: function (ID, code, val, name) {
			var t = name.split(SP);
			var field = t[0],
				pk = t[1];

			$.post('/grid/updateCell', {
				code: code,
				pk: pk,
				field: field,
				val: val,
			}, function (result) {
				if (result.success) {
					// console.log($.str.format('单元格保存成功{0}={1}', name, val));
					$.msg('自动保存成功', {
						offset: 'rb'
					});
				} else {
					// console.error($.str.format('单元格保存失败{0}={1}', name, val));
					$.msg('自动保存失败');
				}
			}, 'json');
		},
		// 更新本地数据
		update: function (ID, index, val, name) {
			var t = name.split(SP);
			var field = t[0],
				pk = t[1];

			layui.use(['table'], function () {
				var table = layui.table;
				// 当前表格实例
				var tableIns = table.getIns(ID);
				// 当前表格本地数据
				var data = tableIns.config.data;

				$.each(data, function (i, o) {
					// 判断行
					if (index == o.LAY_TABLE_INDEX) {
						// 如果存在值域,把值更新到值域上
						var valueFieldName = field + '_val';
						if (valueFieldName in o) {
							o[valueFieldName] = val;
						} else {
							o[field] = val;
						}
					}
				});

				// 更新数据
				//tableIns = table.reload(ID, { data: data });
			});
		},
		// 新增行
		addRow: function (ID) {
			layui.use(['table'], function () {
				var $ = layui.$,
					table = layui.table;

				var tableIns = table.getIns(ID);
				var data = tableIns.config.data;
				// var cols = tableIns.config.cols[0];// 获取table列信息
				var editrow = tableIns.config.editrow; // 可编辑行

				var data = data || [];
				data.push(editrow);
				// data.push({category_val: '',num: 1,}); 手工构造行对象

				// 更新数据
				tableIns = table.reload(ID, {
					data: data
				});
			});
		},
		// 删除行
		delRow: function (ID) {
			layui.use(['table'], function () {
				var $ = layui.$,
					table = layui.table;

				var tableIns = table.getIns(ID);
				var data = tableIns.config.data;

				// 获取选中行的索引
				var index = table.checkIndex(ID);
				if (index == -1) {
					$.msg($.I18N('请选择一行数据'));
					return;
				}

				// 逆向删除(防止下标异常)
				for (var i = data.length - 1; i >= 0; i--) {
					var o = data[i];
					if (index == o.LAY_TABLE_INDEX) {
						data.splice(i, 1);
					}
				}

				// 更新数据
				tableIns = table.reload(ID, {
					data: data
				});

			});
		},
		// 获取表格中正在编辑的数据(仅限手工编辑模式使用)
		getData: function getData(ID) {
			var data;
			layui.use('table', function () {
				data = layui.table.getIns(ID).config.data;
			});
			return data;
		}
	};
})(jQuery);