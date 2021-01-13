<!--#
if(isTrue(isReadonly!)){
    style = style + "background:rgba(218, 218, 218, 0.4)";
}
-->
<div class="eova-texts">
	<textarea id="${id!}" name="${name}" placeholder="${placeholder!}" ${htmlattrs!} style="${style!}" ${isTrue(isReadonly!) ? 'readonly' : ''} >${value!}</textarea>
</div>