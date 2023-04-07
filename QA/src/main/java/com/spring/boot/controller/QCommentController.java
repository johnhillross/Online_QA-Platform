package com.spring.boot.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.boot.domain.Question;
import com.spring.boot.domain.QComment;
import com.spring.boot.domain.User;
import com.spring.boot.service.QuestionService;
import com.spring.boot.service.QCommentService;
import com.spring.boot.util.ConstraintViolationExceptionHandler;
import com.spring.boot.vo.Response;

@Controller
@RequestMapping("/qcomments")
public class QCommentController {
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private QCommentService qcommentService;
	
	/**
	 * 获取评论列表
	 * @param questionId
	 * @param model
	 * @return
	 */
	@GetMapping
	public String listQComments(@RequestParam(value="questionId",required=true) Long questionId, Model model) {
		Question question = questionService.getQuestionById(questionId);
		List<QComment> qcomments = question.getQComments();
		
		// 判断操作用户是否是评论的所有者
		String qcommentOwner = "";
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null) {
				qcommentOwner = principal.getUsername();
			} 
		}
		
		model.addAttribute("qcommentOwner", qcommentOwner);
		model.addAttribute("qcomments", qcomments);
		return "/detail/question :: #qcommentContainerRepleace";
	}
	/**
	 * 发表评论
	 * @param questionId
	 * @param qcommentContent
	 * @return
	 */
	@PostMapping
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> createQComment(Long questionId, String qcommentContent) {
 
		try {
			questionService.createQComment(questionId, qcommentContent);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 删除评论
	 * @return
	 */
	@DeleteMapping("/{id}")
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> delete(@PathVariable("id") Long id, Long questionId) {
		
		boolean isOwner = false;
		User user = qcommentService.getQCommentById(id).getUser();
		
		// 判断操作用户是否是评论的所有者
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null && user.getUsername().equals(principal.getUsername())) {
				isOwner = true;
			} 
		} 
		
		if (!isOwner) {
			return ResponseEntity.ok().body(new Response(false, "没有操作权限"));
		}
		
		try {
			questionService.removeQComment(questionId, id);
			qcommentService.removeQComment(id);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
}