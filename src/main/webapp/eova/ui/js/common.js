/**
 * jQuery Eova Common
 */
(function ($) {

    /**
     * 拓展全局静态方法
     */
    $.extend({
        /** 同步Post **/
        syncPost: function (url, data, success) {
            $.ajax({
                async: false,
                type: 'POST',
                url: url,
                data: data,
                success: success,
                dataType: "json"
            });
        },
        /** 同步获取JSON **/
        syncGetJson: function (url, success) {
            $.ajax({
                async: false,
                type: 'GET',
                url: url,
                success: success,
                dataType: "json"
            });
        },
        /** Html转义 **/
        encodeHtml: function (s) {
            return (typeof s != "string") ? s :
                s.replace(/"|&|'|<|>|[\x00-\x20]|[\x7F-\xFF]|[\u0100-\u2700]/g,
                    function ($0) {
                        var c = $0.charCodeAt(0), r = ["&#"];
                        c = (c == 0x20) ? 0xA0 : c;
                        r.push(c);
                        r.push(";");
                        return r.join("");
                    });
        },
        /** Html过滤 **/
        filterHtml: function (s) {
        	return s.replace(/<[^>]+>/g,"");
        },
        /** 追加URL参数 **/
        appendUrlPara: function (url, key, val) {
        	if(!val || val == ''){
        		return url;
        	}
            if(url.indexOf('?') == -1){
            	url = url + '?';
            } else {
            	url = url + '&';
            }
            return url + key + '=' + val;
        },
        /** 获取URL参数 **/
        getUrlPara: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]); return;
        },
        /** 获取URL QueryString **/
        getUrlParas: function () {
        	var url = location.href;
        	if(url.indexOf("?") == -1){
        		return;
        	}
        	return url.substring(url.indexOf("?")+1,url.length); 
        },
        /** 获取Form参数对象-用于Post请求 **/
        getFormParasObj: function (form) {
        	var o = {};
    		$.each(form.serializeArray(), function(index) {
    			if (o[this['name']]) {
    				o[this['name']] = o[this['name']] + "," + this['value'];
    			} else {
    				o[this['name']] = this['value'];
    			}
    		});
    		return o;
        },
        /** 获取Form参数字符-用于get请求 **/
        getFormParasStr: function (form) {
        	var o = "";
        	$.each(form.serializeArray(), function(index) {
        		var key = this['name'], val = this['value'];
        		if(val && val.length > 0){
        			o = o + key + "=" + val + "&";        			
        		}
        	});
        	return o.substring(0, o.length-1); 
        },
        /** 获取浏览器类型 **/
        getBrowser: function() {
        	var explorer = window.navigator.userAgent;
			if (explorer.indexOf("MSIE") >= 0) {
				return 'ie';
			} else if (explorer.indexOf("Firefox") >= 0) {
				return 'firefox';
			} else if (explorer.indexOf("Chrome") >= 0) {
				return 'chrome';
			} else if (explorer.indexOf("Opera") >= 0) {
				return 'opera';
			} else if (explorer.indexOf("Safari") >= 0) {
				return 'safari';
			}
        },
        /** 格式化自动2位补零，制保留2位小数，如：2，会在2后面补上00.即2.00 **/
		formatDouble : function(x) {
			var f = Math.round(x * 100) / 100;
			var s = f.toString();
			var rs = s.indexOf('.');
			if (rs < 0) {
				rs = s.length;
				s += '.';
			}
			while (s.length <= rs + 2) {
				s += '0';
			}
			return s;
		},
		/**
		 * 格式化JSON
		 * @param txt 
		 * @param compress 是否压缩
		 * @returns
		 */
		jsonformat : function(json, compress) {
			var indentChar = '    ';
			if (/^\s*$/.test(json)) {
				alert('数据为空,无法格式化! ');
				return;
			}
			try {
				var data = eval('(' + json + ')');
			} catch (e) {
				return;
			}
			var draw = [], last = false, This = this, line = compress ? '' : '\n', nodeCount = 0, maxDepth = 0;
			var notify = function(name, value, isLast, indent, formObj) {
				nodeCount++;
				for ( var i = 0, tab = ''; i < indent; i++)
					tab += indentChar;
				tab = compress ? '' : tab;
				maxDepth = ++indent;
				if (value && value.constructor == Array) {
					draw.push(tab + (formObj ? ('"' + name + '":') : '') + '[' + line);
					for ( var i = 0; i < value.length; i++)
						notify(i, value[i], i == value.length - 1, indent, false);
					draw.push(tab + ']' + (isLast ? line : (',' + line)));
				} else if (value && typeof value == 'object') {
					draw.push(tab + (formObj ? ('"' + name + '":') : '') + '{' + line);
					var len = 0, i = 0;
					for ( var key in value)
						len++;
					for ( var key in value)
						notify(key, value[key], ++i == len, indent, true);
					draw.push(tab + '}' + (isLast ? line : (',' + line)));
				} else {
					if (typeof value == 'string')
						value = '"' + value + '"';
					draw.push(tab + (formObj ? ('"' + name + '":') : '') + value + (isLast ? '' : ',') + line);
				}
				;
			};
			var isLast = true, indent = 0;
			notify('', data, isLast, indent, false);
			return draw.join('');
		},
		/**
		 * 自动获取焦点
		 * @param $input
		 * @returns
		 */
		autoFocus : function($input) {
			$input.focus();
			var s = $input.val();
			$input.val("");
			$input.val(s);
		},
		/**
		 * 异步文件上传
		 * @param $input
		 * @returns
		 */
		ajaxUpload : function(url, fileId, fileName, success) {
			var file = document.getElementById(fileId).files[0];
			
			var data = new FormData();
			data = new FormData();
			data.append(fileName, file);
			
			$.ajax({
			    url: url,
			    type: 'POST',
			    cache: false,
			    data: data,
			    processData: false,
			    contentType: false,
			}).done(success);
		},
		/**
		 * Form Validator By Nice
		 */
		configValidator: function($form, fields){
			$form.validator({
	            debug: false,
	            stopOnError: true,
	            focusInvalid: false,
	            showOk: false,
	            timely: false,
	            msgMaker: false,
	            fields: fields
	        });

	        $form.on("validation", $.validation);
		},
		/**
		 * 全局统一关闭弹窗
		 * @param {*} window 
		 */
		dialogClose: function (layer, window) {
			layer.close(layer.getFrameIndex(window.name));
		},
		/**
		 * 全局统一弹窗
		 */
		dialog: function(ID, name, url, width, height) {
			// 自动根据URL进行ID命名
			var id = url.replace(/\//g, '_');
			var i = id.indexOf('?');
			if (i != -1) {
				id = id.substring(0, i - 1);
			}
			
			function bindKey(layero, index, ID) {
				var $layer = layer;

				// 注册键盘事件
				var win = layero.find('iframe').get(0).contentWindow;
				$(win).keyup(function(event) {
					switch (event.keyCode) {
						case 27: {
							// ESC
							$layer.close(index);
						}
						break;
					}
				}).keypress(function(event) {
					if (event.ctrlKey && (event.keyCode == 10 || event.keyCode == 13)) {
						// Ctrl + 回车
						win.btnSaveCallback(layero, ID, parent.$);
					}
				});
			}
			
			var defWidth,defHeight;
			if(width && width > 0 && width <= 1){
				// 支持百分比0-1=0%-100%
				defWidth = width * $(window).width();
			} else {
				// 默认大小(用于初始占位)
				defWidth = (width ? width : 720);
			}
			if(height && height > 0 && height <= 1){
				defHeight= height * $(window).height();
			} else {
				defHeight = (height ? height : 500);
			}
			
			var index = layer.open({
				type : 2,//0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
				id : id,
				title : name,
				content : url,
				maxmin : true,
				resize : true,
				area : [defWidth + 'px', defHeight + 'px'],
				btnAlign : 'c',
				shade : 0.3,
				zIndex : layer.zIndex,
				success : function(layero, index) {
					// layer.setTop(layero);// 窗口置顶
					bindKey(layero, index, ID);
					// 设置焦点
					var $win = layero.find('iframe').get(0).contentWindow;
					$win.focus();

					// 获取弹窗元素 var body = layer.getChildFrame('body', index);
					if (!height) {
						$.dialogAuto(index);
						// 强制修正 添加DOM 到 body 出现滚动条
						var $win = $(layero).find('iframe');
						$(layero).height($(layero).height() + 0.5);
						$win.height($win.height() + 0.5);
					}
				},
				btn : [ '确定', '取消' ],// , '自定义'
				btn1 : function(index, layero) {
					$.countDown(".layui-layer-btn0", 1000, function() {
						layero.find('iframe')[0].contentWindow.btnSaveCallback(layero, ID, parent.$);
					});
				},
				btn2 : function(index, layero) {
					return true;
				}
			// ,
			// btn3: function (index, layero) {
			// alert('自定义业务逻辑');
			// return false;// 不关闭
			// }
			});
		},
		/**
		 * 弹窗高度自适应
		 */
		dialogAuto: function (index) {
			var doms = ['layui-layer', '.layui-layer-title', '.layui-layer-main', '.layui-layer-dialog', 'layui-layer-iframe', 'layui-layer-content', 'layui-layer-btn', 'layui-layer-close'];
			if(!index) return;
			var heg = layer.getChildFrame('html', index).outerHeight();
			var layero = $('#'+ doms[0] + index);
			var titHeight = layero.find(doms[1]).outerHeight() || 0;
			var btnHeight = layero.find('.'+doms[6]).outerHeight() || 0;
			
			var totalHeight = heg + titHeight + btnHeight;
			
			// 弹窗不超过父页面最大高度
			var winHeight = $(window).height();
			// console.log(heg + " - " + winHeight);
			if (totalHeight > winHeight) {
				// 计算最大显示范围
				heg = winHeight - (titHeight + btnHeight) - 10;// 再留个10的缝隙
			}
			// 居中定位
			layero.css({top : (winHeight - (heg + titHeight + btnHeight)) / 2});
			layero.css({height: heg + titHeight + btnHeight});
			layero.find('iframe').css({height: heg});
		},
		/**
		 * 选择器,倒数毫秒,可用回调
		 */
		countDown: function(selector, millis, callback) {
			var $btn = $(selector);
			if (!$btn.hasClass('layui-btn-disabled')) {
				$btn.addClass('layui-btn-disabled');
				var i = 3;
				function count() {
					i--;
					if (i == 0) {
						$btn.removeClass('layui-btn-disabled');
						return;
					}
					setTimeout(function() {
						count()
					}, millis)
				}
				count();

				callback.call();
			}
		},
		/**
		 * 父页面Table刷新
		 * @param Table ID
		 */
		parentTableReLoad: function (ID) {
			parent.layui.use(['table'], function () {
				parent.layui.table.reload(ID);
			});
		},
		/**
		 * 本页面Table刷新
		 */
		tableReLoad: function (ID) {
			layui.use(['table'], function () {
				layui.table.reload(ID);
			});
		},
    });
})(jQuery);

/**
 * JQuery的AJAX默认异常处理
 */
$.ajaxSetup({
	type : 'POST',
	error : function(XMLHttpRequest, textStatus, errorThrown) {
		try {
			parent.$.alert(XMLHttpRequest.responseText);
		} catch (e) {
			alert(XMLHttpRequest.responseText);
		}
	}
});