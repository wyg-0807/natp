<form class="eova-form"  id="${id}" onsubmit="return false;"><!--#// 默认返回false防止浏览器自动提交表单-->
<!--#for(f in items){-->
	<!--#// 只输出允许查询的条件-->
	<!--#if(!isTrue(f.is_query)){continue;}-->
	<div class="eova-form-field">
		<label class="eova-form-label">${f.cn}</label>
		<!--#if(f.type == "文本框" || f.type == "文本域" || f.type == "编辑框" || f.type == "图片框" || f.type == "文件框"){-->
			<#text id="${f.en}" name="query_${f.en}" />
		<!--#}else if(f.type == "日期框"){-->
			<#times id="${f.en}" name="${f.en}" format="yyyy-MM-dd"/>
		<!--#}else if(f.type == "时间框"){-->
			<#times id="${f.en}" name="${f.en}" format="yyyy-MM-dd HH:mm:ss"/>
		<!--#}else if(f.type == "数字框"){-->
			<#text id="${f.en}" name="${f.en}" />
		<!--#}else{-->
			<#field item="${f}" isQuery="true" />
		<!--#}-->
	</div>
<!--#}-->
</form>