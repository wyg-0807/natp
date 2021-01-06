<!--#
var uploadDir = filedir;
//debug(filedir);
var imgUrl = null;
if(!isEmpty(value!)){
	if(isEmpty(uploadDir!)){
		uploadDir = "temp";
	}
	imgUrl = IMG! + uploadDir + '/' + value;
	// debug(imgUrl);
}
-->
<div class="eova-img">
	<input type="text" id="${id!}" name="${name!}" value="${value!}" style="width: 175px;" readonly="readonly"><i class="eova-icon" title="点击选择图片">&#xe64a;</i>
	<input type="file" id="${id!}_file" name="${name!}_file" style="display: none;">
</div>
<div id="eova_img_list_${id!}" style="margin-left: 110px">
	<!--#if(isEmpty(imgUrl!)){-->
	<span>${memo!'待上传...'}</span>
	<!--#}-->
	<img src="${imgUrl!}" title="点击放大查看" height="${height!100}" style="display:${isEmpty(imgUrl!) ? 'none' : 'block'};cursor: pointer;">
</div>

<script>

$(function() {
	var $input = $('#${id!}');
	var $btn = $input.siblings('i');
	var $file = $input.siblings('input');
	var $img = $input.parent().siblings('div').find('img');
	var $memo = $input.parent().siblings('div').find('span');
	
	$input.click(function(){
		$file.click();
	})
	$btn.click(function(){
		$file.click();
	});
	
	// 图片点击放大
	$img.click(function(){
		layer.photos({
		  photos: '#eova_img_list_${id!}',
		  anim: 5
		}); 
	});
	
	var htmlOptions = eval('({${options!}})');
	if (htmlOptions.isReadonly) {
		$btn.hide();
		$input.unbind();
        return;
    }

	var upload = function(){
		
		var url = '/upload/img?name=${name!}_file&filedir=${filedir}&filename=${filename!}';
		
		var reader = new FileReader();
		var files = $file.prop('files');
		reader.readAsDataURL(files[0]);
		
		reader.onload = function(e) {
			$memo.text('上传中,请稍候...');
            $.ajaxUpload(url, "${id!}_file", "${name!}_file", function(o){
            	if (o.state == 'ok') {
            		$img.attr('src', e.target.result);
            		$memo.hide();
            		$img.show();
        			$input.val(o.fileName);
        			console.log('eova-img 上传成功:' + o.fileName);
        		} else {
        			$input.val("");
        			$img.attr('alt', '请重新选择图片上传');
        			$.alert(o.msg);
        		}
            })
		}
	
	};
	
	$(document).on("change","#${id!}_file", upload);
	
});
</script>