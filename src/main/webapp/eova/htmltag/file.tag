<div class="eova-file">
	<input type="text" id="${id!}" name="${name!}" value="${value!}" style="width: 175px;" readonly="readonly"><i class="eova-icon" title="点击选择文件">&#xe681;</i>
	<input type="file" id="${id!}_file" name="${name!}_file" style="display: none;">
</div>

<script>

$(function() {
	var $input = $('#${id!}');
	var $btn = $input.siblings('i');
	var $file = $input.siblings('input');
	
	$input.click(function(){
		$file.click();
	})
	$btn.click(function(){
		$file.click();
	});
	
	var htmlOptions = eval('({${options!}})');
	if (htmlOptions.isReadonly) {
		$btn.hide();
		$input.unbind();
        return;
    }

	var upload = function(){
		
		var url = '/upload/file?name=${name!}_file&filedir=${filedir}&filename=${filename!}';
		
		var $this_file = $("#${id!}_file");
		var reader = new FileReader();
		var files = $this_file.prop('files');
		reader.readAsDataURL(files[0]);
		
		reader.onload = function(e) {
			$.ajaxUpload(url, "${id!}_file", "${name!}_file", function(o){
            	if (o.state == 'ok') {
        			$input.val(o.fileName);
        			console.log('eova-file 上传成功:' + o.fileName);
        		} else {
        			$input.val("");
        			$.alert(o.msg);
        		}
            })
		}
	};
	
	$(document).on("change","#${id!}_file", upload);
	
});
</script>