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
import com.spring.boot.domain.es.EsResource;
import com.spring.boot.service.EsResourceService;
import com.spring.boot.vo.TagVO;

@Controller
@RequestMapping("/resources")
public class ResourceController {
	
	@Autowired
    private EsResourceService esResourceService;
	@GetMapping
	public String listEsResources(
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false) boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="5") int pageSize,
			Model model) {
 
		Page<EsResource> page = null;
		List<EsResource> list = null;
		boolean isEmpty = true; // 系统初始化时，没有博客数据
		try {
			if (order.equals("hot")) { // 最热查询
				Sort sort = new Sort(Direction.DESC,"rviewSize","rcommentSize","rvoteSize","createTime"); 
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = esResourceService.listHotestEsResources(keyword, pageable);
			} else if (order.equals("new")) { // 最新查询
				Sort sort = new Sort(Direction.DESC,"createTime"); 
				Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
				page = esResourceService.listNewestEsResources(keyword, pageable);
			}
			
			isEmpty = false;
		} catch (Exception e) {
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = esResourceService.listEsResources(pageable);
		}  
 
		list = page.getContent();	// 当前所在页面数据列表
 

		model.addAttribute("order", order);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", page);
		model.addAttribute("resourceList", list);
		
		// 首次访问页面才加载
		if (!async && !isEmpty) {
			List<EsResource> newest = esResourceService.listTop5NewestEsResources();
			model.addAttribute("newest", newest);
			List<EsResource> hotest = esResourceService.listTop5HotestEsResources();
			model.addAttribute("hotest", hotest);
			List<TagVO> tags = esResourceService.listTop30Tags();
			model.addAttribute("tags", tags);
			List<User> users = esResourceService.listTop12Users();
			model.addAttribute("users", users);
		}
		
		return (async==true?"/resources :: #resourceContainerRepleace":"/resources");
	}
 
	@GetMapping("/newest")
	public String listNewestEsResources(Model model) {
		List<EsResource> newest = esResourceService.listTop5NewestEsResources();
		model.addAttribute("newest", newest);
		return "newest";
	}
	
	@GetMapping("/hotest")
	public String listHotestEsResources(Model model) {
		List<EsResource> hotest = esResourceService.listTop5HotestEsResources();
		model.addAttribute("hotest", hotest);
		return "hotest";
	}
	
}
