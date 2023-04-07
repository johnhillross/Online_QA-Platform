package com.spring.boot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.spring.boot.domain.User;
import com.spring.boot.domain.AComment;
import com.spring.boot.domain.AVote;
import com.spring.boot.domain.Answer;
import com.spring.boot.service.AnswerService;
import com.spring.boot.service.QuestionService;
import com.spring.boot.util.ConstraintViolationExceptionHandler;
import com.spring.boot.vo.Response;

@Controller
@RequestMapping("/answers")
public class AnswerController {
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
    private AnswerService answerService;
	
	/**
	 * 获取回答列表
	 * @param questionId
	 * @param model
	 * @return
	 */
	@GetMapping
	public String listAnswers(@RequestParam(value="questionId",required=true) Long questionId,
			@RequestParam(value = "answerOrder", required = false, defaultValue = "active") String answerOrder, Model model) {
		
		Question question = questionService.getQuestionById(questionId);
		String questionOwner = question.getUser().getUsername();
		
		Long accAnswerId = 0L;
		accAnswerId = question.getAccAnswerId();
		
		List<Answer> answers = question.getAnswers();
		
		if (answerOrder.equals("active")) {
			Collections.sort(answers, new Comparator<Answer>() {
		      @Override
		      public int compare(Answer answer1, Answer answer2) {
		        return answer2.getCreateTime().compareTo(answer1.getCreateTime());
		      }
		    });
		} else if (answerOrder.equals("votes")) {
			Collections.sort(answers, new Comparator<Answer>() {
		      @Override
		      public int compare(Answer answer1, Answer answer2) {
		        return answer2.getAvoteSize().compareTo(answer1.getAvoteSize());
		      }
		    });
		}
		
		if (accAnswerId != 0) {
			int accAnswerIndex = answers.indexOf(answerService.getAnswerById(accAnswerId));
			if (accAnswerIndex != 0) {
				Collections.swap(answers,0,accAnswerIndex);
			}
		}

		List<AComment> acomments = new ArrayList<AComment>();
		List<AVote> currentAVotes = new ArrayList<AVote>();
		List<Long> currentAVotesId = new ArrayList<Long>();
		
		for (Answer answer : answers) {
			acomments.addAll(answer.getAComments());
		}
		
		// 判断操作用户是否是评论的所有者
		String answerOracommentOwner = "";
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null) {
				answerOracommentOwner = principal.getUsername();
				for (Answer answer : answers) {
					List<AVote> avotes = answer.getAVotes();
					for (AVote avote : avotes) {
						if (avote.getUser().getUsername().equals(principal.getUsername())) {
							currentAVotes.add(avote);
							currentAVotesId.add(answer.getId());
							break;
						}
					}
				}
			} 
		}
		
		model.addAttribute("answerOracommentOwner", answerOracommentOwner);
		model.addAttribute("answers", answers);
		model.addAttribute("acomments", acomments);
		model.addAttribute("currentAVotes", currentAVotes);
		model.addAttribute("currentAVotesId", currentAVotesId);
		model.addAttribute("questionOwner", questionOwner);
		model.addAttribute("accAnswerId", accAnswerId);
		return "/detail/question :: #answerContainerRepleace";
	}
	
	/**
	 * 接受回答
	 * @param answerId
	 * @return
	 */
	@PostMapping("/accept")
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> acceptAnswer(Long questionId, Long answerId) {
		
		try {
			Question question = questionService.getQuestionById(questionId);
			question.setAccAnswerId(answerId);
			questionService.saveQuestion(question);
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
	@DeleteMapping("/accept/{id}")
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> cancel(@PathVariable("id") Long questionId) {
		
		boolean isOwner = false;
		Question question = questionService.getQuestionById(questionId);
		User user = question.getUser();
		
		// 判断操作用户是否是问题的所有者
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
			question.setAccAnswerId(0L);
			questionService.saveQuestion(question);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 发表回答
	 * @param questionId
	 * @param answerContent
	 * @return
	 */
	@PostMapping("/content")
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> createQComment(Long questionId, String answerContent) {
 
		try {
			questionService.createAnswer(questionId, answerContent);
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
	@DeleteMapping("/content/{id}")
	/*
	 * @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')") // 指定角色权限才能操作方法
	 */	public ResponseEntity<Response> delete(@PathVariable("id") Long id, Long questionId) {
		
		boolean isOwner = false;
		User user = answerService.getAnswerById(id).getUser();
		
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
			questionService.removeAnswer(questionId, id);
			answerService.removeAnswer(id);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
}
