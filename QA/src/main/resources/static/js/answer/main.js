"use strict";

//DOM 加载完再执行
$(function() {
	
	for (var id = 1;id <= answerSize; id++)
	if ($('#markdown-answer-view-'+id).length > 0) {
		var AnswerView = editormd.markdownToHTML('markdown-answer-view-'+id, {
	        htmlDecode : true,  // Enable / disable HTML tag encode.
	        htmlDecode : "style,script,iframe",  // Note: If enabled, you should filter some dangerous HTML tags for website security.
	        emoji : true,
	        taskList : true,
            tex : true,
            /*flowChart : true,
            sequenceDiagram : true,*/
	    });
	}

});