<!--#
// 查询控件name自动追加前缀
var name = item.en;
if(isTrue(isQuery!)){
	name = "query_" + name;
}
-->
<!--#if(item.type == "下拉框"){-->
	<#combo id="${item.en}" name="${name}" code="${item.object_code}" field="${item.en}" value="${value!}" multiple="${item.is_multiple}" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "下拉树"){-->
	<#combotree id="${item.en}" name="${name}" code="${item.object_code}" field="${item.en}" value="${value!}" multiple="${item.is_multiple}" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "查找框"){-->
    <#find id="${item.en}" name="${name}" code="${item.object_code}" field="${item.en}" value="${value!}" multiple="${item.is_multiple}" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "时间框"){-->
	<!--#if(!isEmpty(value!)){value = strutil.formatDate(value, 'yyyy-MM-dd HH:mm:ss');}-->
    <#time id="${item.en}" name="${name}" value="${value!}" isReadonly="${readnoly!false}" options="format:'yyyy-MM-dd HH:mm:ss'" />
<!--#} else if(item.type == "日期框"){-->
	<!--#if(!isEmpty(value!)){value = strutil.formatDate(value, 'yyyy-MM-dd');}-->
    <#time id="${item.en}" name="${name}" value="${value!}" isReadonly="${readnoly!false}" options="format:'yyyy-MM-dd'"/>
<!--#} else if(item.type == "文本域"){-->
	<#texts id="${item.en}" name="${name}" value="${value!}" placeholder="${item.placeholder!}" validator="${item.validator!}" style="width:532px;height:${item.height!40}px;" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "编辑框"){-->
    <#edit id="${item.en}" name="${name}" value="${value!}" style="width: 80%;height:${item.height!250}px;" isReadonly="${readnoly!false}" meta="${item}"/>
<!--#} else if(item.type == "布尔框"){-->
	<#bool id="${item.en}" name="${name}" value="${value!}" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "图片框"){-->
	<#img id="${item.en}" name="${name}" value="${value!}" options="isReadonly : ${readnoly!false}" filedir="${item.config.filedir!}" filename="${item.config.filename!}" memo="${item.config.memo!}" height="${item.height!100}" />
<!--#} else if(item.type == "多图框"){-->
	<#imgs id="${item.en}" name="${name}" value="${value!}" options="isReadonly : ${readnoly!false}" filedir="${item.config.filedir!}" imgWidth="${item.config.width!}" imgHeight="${item.config.height! }" />
<!--#} else if(item.type == "文件框"){-->
	<#file id="${item.en}" name="${name}" value="${value!}" options="isReadonly : ${readnoly!false}" filedir="${item.config.filedir!}" filename="${item.config.filename!}" />
<!--#} else if(item.type == "图标框"){-->
    <#icon id="${item.en}" name="${name}" value="${value!}" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "自增框" || item.type == "文本框" || item.type == "数字框"){-->
    <#text id="${item.en}" name="${name}" value="${value!}" placeholder="${item.placeholder!}" validator="${item.validator!}" options="" isReadonly="${readnoly!false}" />
<!--#} else if(item.type == "JSON框"){-->
	<#json id="${item.en}" name="${name}" value="${value!}" style="height:${item.height > 200 ? item.height : 200}px;" isReadonly="${readnoly!false}" />
<!--#} else {// 默认为文本框-->
	<!--# var widgets = query("widget"); -->
    <!--#for(widget in widgets){-->
	    <!--#if(widget.value == item.type){-->
	    <div class="eova-widget"><!--#include(widget.path, {'field' : item, 'name' : name, 'value' : value!}){}--></div>
	    <!--#}-->
    <!--#}-->
<!--#}-->