<table id="${id}" lay-filter="${id}"></table>
<script>
// var ${'$'+id};
var ${id!}Ins;// Layui Table 对象实例
$(function () {
    // init param
    var ID = "${id!}";
    var PID = "${pid!}";
    var $grid = $("#" + ID);
    var $parentWidget;
    if(PID != ""){
    	$parentWidget = $("#" + PID);
    }
    
    // 离线数据模式
    var isData = ${isEmpty(data!) ? false : true};

    var menuCode = '${menuCode!}';
    var objectCode = '${objectCode!}';
    var toolbar = '${toolbar!}';// grid ref toolbar
    var isPaging = eval('${isPaging!true}');// is show pagination
    var url = '${url!}';// diy grid load data url
    var height = '${height!}';// diy grid load data url
		
    var objectJson = '${objectJson!}'.replace('"{', '{').replace('}"', '}');// object is json
    var fieldsJson = '${fieldsJson!}';// fiedlds is json
    var configJson = '${configJson!}';// config is json

    if (url == '') {
        url = '/grid/query/' + objectCode;
        if(menuCode != ''){
        	url = url + '-' + menuCode;
        }
    }
    var paras = $.getUrlParas();
    // 自动传递所有参数
    // 是否含有关联查询条件
    if(paras && (paras.indexOf('query_') != -1 || paras.indexOf('filter_') != -1)){
    	url = url + '?' + paras;
    }
    
	// 初始化组件
	EovaWidget.init(objectCode, objectJson, fieldsJson, configJson);
    var config = EovaWidget.data.config,
    	object = EovaWidget.data.object,
    	fields = EovaWidget.data.fields;

    // 当前对象是否允许初始加载数据
    var isFirstLoad = false;
    var isFirstLoadNow = eval('${isFirstLoad!true}');
	// 必须当前业务和对象都允许加载数据
    if(isFirstLoadNow && object.is_first_load){
    	isFirstLoad = true;
    }

    var cols = [];
    // var validators = {};
    
    // 构建首列
    $.table.buildFirstColumn(object, fields, cols);
    // 构建其它列
    $.table.buildColumn(object, fields, cols);
    
    layui.use(['table', 'form'], function () {
    	var table = layui.table,
    		form = layui.form;

    	tableIns = table.render({
    		id: ID,
    		elem: '#' + ID,
            toolbar: '${isEmpty(toolbar!) ? '' : '#' + toolbar}',
    		${isEmpty(data!) ? 'url: url,' : 'data: eval('+ data +'),'}
    		cols: [cols],
    		loading: true,
    		isFirstLoad: isFirstLoad,
    		title: object.name,
    		limits: [15,30,50,100,200,500,1000,2000],
    		limit: isPaging ? 15 : 5000,
    		page: isPaging,
    		autoSort: false,
    		// hide: true,
    		totalRow: config.totalRow || false ,
    		height: height,
    		text: {
    			none: '暂无相关数据'
    		},
    		<!--#// 条件的两种玩法:1.传函数名,2传JSON-->
    		${isEmpty(where!) ? '' : 'where: eval('+ where +'),'}
    		// defaultToolbar: ['filter', 'print', 'exports'],
    		<!--#// Table自定义按钮-->
    		<!--#if(isTrue(isDiyToolbar!true)){-->
    		diyToolbar: $.table.buildDiyToolbar(${session.user.isAdmin}),
    		<!--#}-->
    		done: function(res, curr, count){
    			// 数据加载成功回调
    			try {${id!}Done(res, curr, count);} catch(e) {}
    			
    			// 控件初始化
    		    $.initEovaUI();
    			// 单元格自动保存
    			if (isData) {
    				// 手动处理模式,数据不做自动持久化,更新到 table.config.data 中,手工获取提交服务端
    				objectCode = '';
				}
    		    $.table.process(ID, objectCode);
    		}
    	});
    	
    	// 监听排序事件 
    	table.on('sort(' + ID + ')', function(obj){
    		table.reload(ID, {
	    	    initSort: obj, //记录初始排序，如果不设的话，将无法标记表头的排序状态。
	    	    where: { //请求参数（注意：这里面的参数可任意定义，并非下面固定的格式）
	    	      field: obj.field, //排序字段
	    	      order: obj.type //排序方式
	    	    }
			});
    	});

    	table.on('toolbar(' + ID + ')', function (obj) {
    		$.table.diyToolbarEvent(obj, object, menuCode, ID, table);
    		// 工具栏事件回调
			try {${id!}Toolbar(obj);} catch(e) {}
    	});

        // 单击事件:选中当前行
		table.on('row(' + ID + ')', function(obj) {
			// 单选模式,单击行选中
			if (object.is_single) {
				table.checkRow(ID, obj.index);
			}
			// 选中行回调
			try {${id!}Select(obj);} catch(e) {}
		});
    	
		// 动态生成可单元格编辑列对象模型
		tableIns.config.editrow = {
			category_val: '',
			num: 1,
		}
		
		// 构建单元格编辑行
	    $.table.buildEditRow(tableIns, fields);
    	
    	// table实例对象外放,方便页面内使用
		${id!}Ins = tableIns;
    });
});	
</script>