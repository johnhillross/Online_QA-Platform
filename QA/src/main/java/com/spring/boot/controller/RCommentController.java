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

import com.spring.boot.service.ResourceService;
import com.spring.boot.util.ConstraintViolationExceptionHandler;
import com.spring.boot.vo.Response;
import com.spring.boot.domain.Resource;
import com.spring.boot.domain.RComment;
import com.spring.boot.domain.User;
import com.spring.boot.service.RCommentService;

@Controller
@RequestMapping("/rcomments")
public class RCommentController {
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private RCommentService rcommentService;
	
	/**
	 * 获取评论列表
	 * @param resourceId
	 * @param model
	 * @return
	 */
	@GetMapping
	public String listRComments(@RequestParam(value="resourceId",required=true) Long resourceId, Model model) {
		Resource resource = resourceService.getResourceById(resourceId);
		List<RComment> rcomments = resource.getRcomments();
		
		// 判断操作用户是否是评论的所有者
		String rcommentOwner = "";
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null) {
				rcommentOwner = principal.getUsername();
			} 
		}
		
		model.addAttribute("rcommentOwner", rcommentOwner);
		model.addAttribute("rcomments", rcomments);
		return "/detail/resource :: #rcommentContainerRepleace";
	}
	/**
	 * 发表评论
	 * @param resourceId
	 * @param rcommentContent
	 * @return
	 */
	@PostMapping
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> createRComment(Long resourceId, String rcommentContent) {
 
		try {
			resourceService.createRComment(resourceId, rcommentContent);
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
	 */	public ResponseEntity<Response> delete(@PathVariable("id") Long id, Long resourceId) {
		
		boolean isOwner = false;
		User user = rcommentService.getRCommentById(id).getUser();
		
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
			resourceService.removeRComment(resourceId, id);
			rcommentService.removeRComment(id);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
}
