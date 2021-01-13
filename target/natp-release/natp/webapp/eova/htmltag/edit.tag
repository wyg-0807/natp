<div class="eova-widget">
	<textarea id="${id!}" name="${name!}" style="${style!}">${value!}</textarea>
</div>
<style>.menu-item {overflow: visible!important;}</style>
<script type="text/javascript">
	$(function(){
		var editor = new wangEditor('${id!}');
		// 配置上传图片
		editor.config.uploadImgUrl = '/upload/editor?object_code=${meta.object_code}&en=${meta.en}';
		// editor.config.hideLinkImg = true;
		editor.create();
		${isTrue(isReadonly!) ? 'editor.disable();' : ''}
	});
</script>