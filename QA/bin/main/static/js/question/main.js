"use strict";

//DOM 加载完再执行
$(function() {
	
	var _pageSize; // 存储用于搜索
	
	// 根据用户名、页面索引、页面大小获取用户列表
	function getQuestionsByName(pageIndex, pageSize) {
		 $.ajax({ 
			 url: "/questions", 
			 contentType : 'application/json',
			 data:{
				 "async":true, 
				 "pageIndex":pageIndex,
				 "pageSize":pageSize,
				 "keyword":$("#indexkeyword").val()
			 },
			 success: function(data){
				 $("#questionContainer").html(data);
				 
				 var keyword = $("#indexkeyword").val();
				 
				 // 如果是分类查询，则取消最新、最热选中样式
				 if (keyword.length > 0) {
					$(".nav .QA-hot-new").removeClass("active");
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	}
	
	// 分页
	$.tbpage("#questionContainer", function (pageIndex, pageSize) {
		getQuestionsByName(pageIndex, pageSize);
		_pageSize = pageSize;
	});
   
	// 关键字搜索
	$("#indexsearch").click(function() {
		getQuestionsByName(0, _pageSize);
	});
	
	// 最新\最热切换事件
	$(".nav .QA-hot-new").click(function() {
 
		var url = $(this).attr("url");
		
		// 先移除其他的点击样式，再添加当前的点击样式
		$(".nav .QA-hot-new").removeClass("active");
		$(this).addClass("active");  
 
		// 加载其他模块的页面到右侧工作区
		 $.ajax({ 
			 url: url+'&async=true', 
			 success: function(data){
				 $("#questionContainer").html(data);
			 },
			 error : function() {
				 toastr.error("error!");
			 }
		 })
		 
		 // 清空搜索框内容
		 $("#indexkeyword").val('');
	});
	
	$(".QA-container").on("click",".QA-add-qcomment", function () {
		var e = document.getElementById('QA-qcomment');
		if (e.style.display=="none") {
			e.style.display="block"
		} else {
			e.style.display="none"
		}
	});
	
	if ($('#markdown-question-view').length > 0) {
		var QuestionView = editormd.markdownToHTML("markdown-question-view", {
	        htmlDecode : true,  // Enable / disable HTML tag encode.
	        htmlDecode : "style,script,iframe",  // Note: If enabled, you should filter some dangerous HTML tags for website security.
	        emoji : true,
	        taskList : true,
            tex : true,
            /*flowChart : true,*/
            /*sequenceDiagram : true,*/
	    });
	}
	
	// 处理删除博客事件
	$(".QA-container").on("click",".question-delete-question", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		
		$.ajax({ 
			 url: questionUrl, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 // 成功后，重定向
					 window.location = data.body;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 获取评论列表
	function getQComment(questionId) {
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({ 
			 url: '/qcomments', 
			 type: 'GET', 
			 data:{"questionId":questionId},
			 beforeSend: function(request) {
	             request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
	         },
			 success: function(data){
				$("#qcommentContainer").html(data);
	
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	}
	
	// 提交评论
	$(".QA-container").on("click","#submitQComment", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/qcomments', 
			 type: 'POST', 
			 data:{"questionId":questionId, "qcommentContent":$('#qcommentContent').val()},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					// 清空评论框
					 $('#qcommentContent').val('');
					 // 隐藏评论框
					 var e = document.getElementById('QA-qcomment');
					 e.style.display="none";
					 // 获取评论列表
					 getQComment(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 删除评论
	$(".QA-container").on("click",".question-delete-qcomment", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/qcomments/'+$(this).attr("qcommentId")+'?questionId='+questionId, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 // 获取评论列表
					 getQComment(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 提交点赞
	$(".QA-container").on("click","#submitQUpVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/qvotes/upvote', 
			 type: 'POST', 
			 data:{"questionId":questionId},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
						// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 取消点赞
	$(".QA-container").on("click","#cancelQUpVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/qvotes/upvote/'+$(this).attr('qvoteId')+'?questionId='+questionId, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 提交点赞
	$(".QA-container").on("click","#submitQDownVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/qvotes/downvote', 
			 type: 'POST', 
			 data:{"questionId":questionId},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
						// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 取消点赞
	$(".QA-container").on("click","#cancelQDownVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/qvotes/downvote/'+$(this).attr('qvoteId')+'?questionId='+questionId, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 初始化 问题评论
	getQComment(questionId);
	
	// 回答排序选项
	$(".QA-container").on("click","#activeOption", function () {
		var e1 = document.getElementById('activeOption');
		e1.style="border: 1px solid #e5e5e5; border-top: 3px solid #FF7800; border-bottom-color: transparent; padding: 5px 20px 5px 20px;"
		var e2 = document.getElementById('votesOption');
		e2.style="border: 1px solid transparent; padding: 5px 20px 5px 20px;"
	});
	
	$(".QA-container").on("click","#votesOption", function () {
		var e1 = document.getElementById('activeOption');
		e1.style="border: 1px solid transparent; padding: 5px 20px 5px 20px;"
		var e2 = document.getElementById('votesOption');
		e2.style="border: 1px solid #e5e5e5; border-top: 3px solid #FF7800; border-bottom-color: transparent; padding: 5px 20px 5px 20px;"
	});
	
	// 回答按发布时间降续排序
	$(".QA-container").on("click","#sortByActive", function () {
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({ 
			 url: '/answers?answerOrder=active', 
			 type: 'GET', 
			 data:{"questionId":questionId},
			 beforeSend: function(request) {
	             request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
	         },
			 success: function(data){
				$("#answerContainer").html(data);
	
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 回答按投票数降续排序
	$(".QA-container").on("click","#sortByVotes", function () {
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({ 
			 url: '/answers?answerOrder=votes', 
			 type: 'GET', 
			 data:{"questionId":questionId},
			 beforeSend: function(request) {
	             request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
	         },
			 success: function(data){
				$("#answerContainer").html(data);
	
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 获取回答列表
	function getAnswer(questionId) {
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({ 
			 url: '/answers', 
			 type: 'GET', 
			 data:{"questionId":questionId},
			 beforeSend: function(request) {
	             request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
	         },
			 success: function(data){
				$("#answerContainer").html(data);
				var e1 = document.getElementById('activeOption');
				e1.style="border: 1px solid #e5e5e5; border-top: 3px solid #FF7800; border-bottom-color: transparent; padding: 5px 20px 5px 20px;"
				var e2 = document.getElementById('votesOption');
				e2.style="border: 1px solid transparent; padding: 5px 20px 5px 20px;"
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	}
	
	// 提交回答
	$(".QA-container").on("click","#submitAnswer", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/answers/content', 
			 type: 'POST', 
			 data:{"questionId":questionId, "answerContent":$('#answerContent').val()},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					// 清空评论框
					 $('#answerContent').val('');
					// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 删除回答
	$(".QA-container").on("click",".question-delete-answer", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/answers/content/'+$(this).attr("answerId")+'?questionId='+questionId, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 接受回答
	$(".QA-container").on("click","#acceptAnswer", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/answers/accept', 
			 type: 'POST', 
			 data:{"questionId":questionId, "answerId":$(this).attr("acceptAnswerId")},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 拒绝已经接受的回答
	$(".QA-container").on("click","#cancelAnswer", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/answers/accept/'+questionId, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					// 成功后，重定向
					 window.location = questionUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 初始化 回答评论
	getAnswer(questionId);
	
	$(".QA-container").on("click",".QA-add-acomment", function () {
		var e = document.getElementById('QA-acomment-'+$(this).attr("answerCount"));
		if (e.style.display=="none") {
			e.style.display="block"
		} else {
			e.style.display="none"
		}
	});
	
	// 提交评论
	$(".QA-container").on("click","#submitAComment", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/acomments', 
			 type: 'POST', 
			 data:{"answerId":$(this).attr("answerId"), "acommentContent":$('#acommentContent-'+$(this).attr("answerId")).val()},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					// 清空评论框
					 $('#acommentContent-'+$(this).attr("answerId")).val('');
					 // 获取评论列表
					 getAnswer(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 删除评论
	$(".QA-container").on("click",".answer-delete-acomment", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/acomments/'+$(this).attr("acommentId")+'?answerId='+$(this).attr("answerId"), 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 // 获取评论列表
					 getAnswer(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 提交点赞
	$(".QA-container").on("click","#submitAUpVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/avotes/upvote', 
			 type: 'POST', 
			 data:{"answerId":$(this).attr("answerId")},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					 // 获取评论列表
					 getAnswer(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 取消点赞
	$(".QA-container").on("click","#cancelAUpVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/avotes/upvote/'+$(this).attr('avoteId')+'?answerId='+$(this).attr("answerId"), 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					 // 获取评论列表
					 getAnswer(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 提交点赞
	$(".QA-container").on("click","#submitADownVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/avotes/downvote', 
			 type: 'POST', 
			 data:{"answerId":$(this).attr("answerId")},
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					 // 获取评论列表
					 getAnswer(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 取消点赞
	$(".QA-container").on("click","#cancelADownVote", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/avotes/downvote/'+$(this).attr('avoteId')+'?answerId='+$(this).attr("answerId"), 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					 // 获取评论列表
					 getAnswer(questionId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
});