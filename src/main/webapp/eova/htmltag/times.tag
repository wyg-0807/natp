<div class="eova-times">
	<input id="start_${id}" name="start_${name}" type="text" onFocus="start_${id}TimeFocus()">
	-
	<input id="end_${id}" name="end_${name}" type="text" onFocus="end_${id}TimeFocus()">
</div>
<script>
	function start_${id}TimeFocus(){
		var end = $dp.$('end_${id}');
		WdatePicker({
			onpicked : function() {
				$dp.$('end_${id}').focus();
			},
			maxDate : '#F{$dp.$D(\'end_${id}\')}',
			dateFmt : '${format}'
		})
	}
	function end_${id}TimeFocus(){
		WdatePicker({minDate:'#F{$dp.$D(\'start_${id}\')}', dateFmt: '${format}' })
	}
</script>