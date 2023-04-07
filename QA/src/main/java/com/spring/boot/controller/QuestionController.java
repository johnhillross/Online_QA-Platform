package com.spring.boot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.boot.domain.User;
import com.spring.boot.domain.es.EsQuestion;
import com.spring.boot.service.EsQuestionService;
import com.spring.boot.vo.TagVO;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	
	@Autowired
    private EsQuestionService esQuestionService;
	
	@GetMapping
	public String listEsQuestions(
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false) boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="5") int pageSize,
			Model model) {
		
		Page<EsQuestion> page = null;
		List<EsQuestion> list = null;
		boolean isEmpty = true; // 系统初始化时，没有博客数据
		try {
			if (order.equals("hot")) { // 最热查询
				Sort sort = new Sort(Direction.DESC,"answerSize","viewSize","qcommentSize","qvoteSize","createTime"); 
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = esQuestionService.listHotestEsQuestions(keyword, pageable);
			} else if (order.equals("new")) { // 最新查询
				Sort sort = new Sort(Direction.DESC,"createTime"); 
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = esQuestionService.listNewestEsQuestions(keyword, pageable);
			}
			
			isEmpty = false;
		} catch (Exception e) {
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = esQuestionService.listEsQuestions(pageable);
		}  
 
		list = page.getContent();	// 当前所在页面数据列表
 

		model.addAttribute("order", order);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("questionList", list);
		
		// 首次访问页面才加载
		if (!async && !isEmpty) {
			List<EsQuestion> newest = esQuestionService.listTop5NewestEsQuestions();
			model.addAttribute("newest", newest);
			List<EsQuestion> hotest = esQuestionService.listTop5HotestEsQuestions();
			model.addAttribute("hotest", hotest);
			List<TagVO> tags = esQuestionService.listTop30Tags();
			model.addAttribute("tags", tags);
			List<User> users = esQuestionService.listTop12Users();
			model.addAttribute("users", users);
		}
		
		return (async==true?"/questions :: #questionContainerRepleace":"/questions");
	}
	
	@GetMapping("/newest")
	public String listNewestEsQuestions(Model model) {
		List<EsQuestion> newest = esQuestionService.listTop5NewestEsQuestions();
		model.addAttribute("newest", newest);
		return "newest";
	}
	
	@GetMapping("/hotest")
	public String listHotestEsQuestions(Model model) {
		List<EsQuestion> hotest = esQuestionService.listTop5HotestEsQuestions();
		model.addAttribute("hotest", hotest);
		return "hotest";
	}

}
