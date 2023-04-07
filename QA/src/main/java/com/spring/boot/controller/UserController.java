package com.spring.boot.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.spring.boot.domain.User;
import com.spring.boot.service.UserService;
import com.spring.boot.util.ConstraintViolationExceptionHandler;
import com.spring.boot.vo.Response;
import com.spring.boot.domain.Vote;
import com.spring.boot.domain.QVote;
import com.spring.boot.domain.RVote;
import com.spring.boot.domain.es.EsBlog;
import com.spring.boot.domain.es.EsQuestion;
import com.spring.boot.domain.es.EsResource;
import com.spring.boot.service.BlogService;
import com.spring.boot.service.EsBlogService;
import com.spring.boot.service.EsQuestionService;
import com.spring.boot.service.EsResourceService;
import com.spring.boot.service.QuestionService;
import com.spring.boot.service.ResourceService;
import com.spring.boot.service.CatalogService;
import com.spring.boot.domain.Answer;
import com.spring.boot.domain.Blog;
import com.spring.boot.domain.Catalog;
import com.spring.boot.domain.Question;
import com.spring.boot.domain.Resource;

@Controller
@RequestMapping("/user")
public class UserController {

	@Value("${UPLOAD_PATH}")
	private String uploadPath;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private BlogService blogService;

	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private ResourceService resourceService;

	@Autowired
	private EsBlogService esBlogService;

	@Autowired
	private EsQuestionService esQuestionService;
	
	@Autowired
	private EsResourceService esResourceService;

	@Autowired
	private CatalogService catalogService;

	@GetMapping("/{username}")
	public ModelAndView userDetail(@PathVariable("username") String username,
			@RequestParam(value = "tab", required = false, defaultValue = "Overview") String tab,
			@RequestParam(value = "order", required = false, defaultValue = "new") String order,
			@RequestParam(value = "catalog", required = false) Long catalogId,
			@RequestParam(value = "state", required = false, defaultValue = "all") String state,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "async", required = false) boolean async,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize, Model model) {

		User user = (User) userDetailsService.loadUserByUsername(username);

		switch (tab) {

		case "Blogs":
			{
				Page<Blog> page = null;

				if (catalogId != null && catalogId > 0) { // 分类查询
					Catalog catalog = catalogService.getCatalogById(catalogId);
					Pageable pageable = new PageRequest(pageIndex, pageSize);
					page = blogService.listBlogsByCatalog(catalog, pageable);
					order = "";
				} else if (order.equals("hot")) { // 最热查询
					Sort sort = new Sort(Direction.DESC, "readSize", "commentSize", "voteSize");
					Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
					page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
				} else if (order.equals("new")) { // 最新查询
					Pageable pageable = new PageRequest(pageIndex, pageSize);
					page = blogService.listBlogsByTitleVote(user, keyword, pageable);
				}
	
				List<Blog> list = page.getContent(); // 当前所在页面数据列表
	
				model.addAttribute("user", user);
				model.addAttribute("tab", tab);
				model.addAttribute("order", order);
				model.addAttribute("catalogId", catalogId);
				model.addAttribute("keyword", keyword);
				model.addAttribute("page", page);
				model.addAttribute("blogList", list);
	
				return new ModelAndView(async == true ? "/detail/user :: #mainContainerRepleace" : "/detail/user",
						"userModel", model);
			}

		case "QA":
			{
				Page<Question> page = null;
				
				if(!state.equals("all")) {
					Pageable pageable = new PageRequest(pageIndex, pageSize);
					page = questionService.listQuestionsByState(user, state, pageable);
					order = "";
				}else if (order.equals("hot")) { // 最热查询
					Sort sort = new Sort(Direction.DESC,"answerSize","viewSize","qcommentSize","qvoteSize","createTime"); 
					Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
					page = questionService.listQuestionsByTitleQVoteAndSort(user, keyword, pageable);
				} else if (order.equals("new")) { // 最新查询
					Pageable pageable = new PageRequest(pageIndex, pageSize);
					page = questionService.listQuestionsByTitleQVote(user, keyword, pageable);
				}
				
				List<Question> list = page.getContent(); // 当前所在页面数据列表
				
				model.addAttribute("user", user);
				model.addAttribute("tab", tab);
				model.addAttribute("order", order);
				model.addAttribute("keyword", keyword);
				model.addAttribute("page", page);
				model.addAttribute("questionList", list);
	
				return new ModelAndView(async == true ? "/detail/user :: #questionContainerRepleace" : "/detail/user",
						"userModel", model);
			}
			
		case "Share":
			
			model.addAttribute("user", user);
			model.addAttribute("tab", tab);
			
			return new ModelAndView(async == true ? "/detail/user :: #shareContainerRepleace" : "/detail/user",
					"userModel", model);

		default:
			model.addAttribute("user", user);
			model.addAttribute("tab", tab);

			return new ModelAndView("/detail/user", "userModel", model);
		}
	}

	@GetMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView profile(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("/detail/profile", "userModel", model);
	}

	@PostMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveProfile(@PathVariable("username") String username, User user,
			@RequestParam("newavatar") MultipartFile newavatar) {
		User originalUser = userService.getUserById(user.getId());
		originalUser.setEmail(user.getEmail());

		// 判断密码是否做了变更
		String rawPassword = originalUser.getPassword();
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePasswd = encoder.encode(user.getPassword());
		boolean isMatch = encoder.matches(rawPassword, encodePasswd);
		if (!isMatch) {
			originalUser.setEncodePassword(user.getPassword());
		}

		// 设置头像
		if (newavatar.getSize() != 0) {
			String avatarName = newavatar.getOriginalFilename();
			String avatarType = avatarName.substring(avatarName.lastIndexOf("."), avatarName.length());
			String checkType = avatarType.substring(1, avatarType.length()).toLowerCase();
			if (checkType.equals("dwg") || checkType.equals("dxf") || checkType.equals("gif") || checkType.equals("jp2") || checkType.equals("jpe") || checkType.equals("jpeg") || checkType.equals("jpg") || checkType.equals("png") || checkType.equals("svf") || checkType.equals("tif") || checkType.equals("tiff")) {
				String avatarid = originalUser.getAvatarid();
				avatarName = avatarid + avatarType;
				String avatarPath = uploadPath + "/avatar/" + avatarName;
				System.out.println("avatarPath:" + avatarPath);
				File file = new File(avatarPath);
				try {
					newavatar.transferTo(file);
				} catch (IOException e) {
					return ResponseEntity.ok().body(new Response(false, "upload failed"));
				}
				originalUser.setAvatar(avatarName);
			}else {
				return ResponseEntity.ok().body(new Response(false, "file's type must be image"));
			}
		}

		userService.saveUser(originalUser);
		String redirectUrl = "/user/" + username;
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

	@GetMapping("/{username}/blogs/{id}")
	public String getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {

		User principal = null;
		Blog blog = blogService.getBlogById(id);

		Page<EsBlog> page = null;
		List<EsBlog> list = null;

		Sort sort = new Sort(Direction.DESC, "readSize", "commentSize", "voteSize", "createTime");
		Pageable pageable = new PageRequest(0, 3, sort);
		page = esBlogService.listHotestEsBlogs(blog.getTags(), pageable);

		list = page.getContent();

		// 每次读取，简单的可以认为阅读量增加1次
		blogService.readingIncrease(id);

		// 判断操作用户是否是博客的所有者
		boolean isBlogOwner = false;
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !SecurityContextHolder
						.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal != null && username.equals(principal.getUsername())) {
				isBlogOwner = true;
			}
		}

		// 判断操作用户的点赞情况
		List<Vote> votes = blog.getVotes();
		Vote currentVote = null; // 当前用户的点赞情况

		if (principal != null) {
			for (Vote vote : votes) {
				vote.getUser().getUsername().equals(principal.getUsername());
				currentVote = vote;
				break;
			}
		}

		model.addAttribute("isBlogOwner", isBlogOwner);
		model.addAttribute("blogModel", blog);
		model.addAttribute("currentVote", currentVote);
		model.addAttribute("blogList", list);

		return "/detail/blog";
	}

	@DeleteMapping("/{username}/blogs/{id}")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {

		try {
			Blog blog = blogService.getBlogById(id);
			System.out.println("blog.getCover():" + blog.getCover());
			File file = new File(uploadPath + "/cover/" + blog.getCover());
			file.delete();
			blogService.removeBlog(id);
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/user/" + username;
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

	@GetMapping("/{username}/blogs/edit")
	public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);

		model.addAttribute("blog", new Blog(null, null, null));
		model.addAttribute("catalogs", catalogs);
		model.addAttribute("user", user);
		return new ModelAndView("/detail/blogedit", "blogModel", model);
	}

	@GetMapping("/{username}/blogs/edit/{id}")
	public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
		// 获取用户分类列表
		User user = (User) userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);

		model.addAttribute("blog", blogService.getBlogById(id));
		model.addAttribute("catalogs", catalogs);
		model.addAttribute("user", user);
		return new ModelAndView("/detail/blogedit", "blogModel", model);
	}

	@PostMapping("/{username}/blogs/edit")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, Blog blog,
			@RequestParam("setcover") MultipartFile setcover) {

		User user = (User) userDetailsService.loadUserByUsername(username);

		// 对 Catalog 进行空处理
		if (blog.getCatalog().getId() == null) {
			return ResponseEntity.ok().body(new Response(false, "未选择分类"));
		}

		try {
			// 判断是修改还是新增
			if (blog.getId() != null) {
				Blog orignalBlog = blogService.getBlogById(blog.getId());

				// 设置封面
				if (setcover.getSize() != 0) {
					String coverName = setcover.getOriginalFilename();
					String coverType = coverName.substring(coverName.lastIndexOf("."), coverName.length());
					String checkType = coverType.substring(1, coverType.length()).toLowerCase();
					if (checkType.equals("dwg") || checkType.equals("dxf") || checkType.equals("gif") || checkType.equals("jp2") || checkType.equals("jpe") || checkType.equals("jpeg") || checkType.equals("jpg") || checkType.equals("png") || checkType.equals("svf") || checkType.equals("tif") || checkType.equals("tiff")) {
						String coverid = orignalBlog.getCoverid();
						coverName = coverid + coverType;
						String coverPath = uploadPath + "/cover/" + coverName;
						System.out.println("coverPath:" + coverPath);
						File file = new File(coverPath);
						try {
							setcover.transferTo(file);
						} catch (IOException e) {
							return ResponseEntity.ok().body(new Response(false, "Upload failed"));
						}
						orignalBlog.setCover(coverName);
					}else {
						return ResponseEntity.ok().body(new Response(false, "file's type must be image"));
					}
				}

				orignalBlog.setTitle(blog.getTitle());
				orignalBlog.setContent(blog.getContent());
				orignalBlog.setSummary(blog.getSummary());
				orignalBlog.setCatalog(blog.getCatalog());
				orignalBlog.setTags(blog.getTags());

				blogService.saveBlog(orignalBlog);
			} else {
				blog.setUser(user);

				// 设置封面
				if (setcover.getSize() != 0) {
					String coverName = setcover.getOriginalFilename();
					String coverType = coverName.substring(coverName.lastIndexOf("."), coverName.length());
					String checkType = coverType.substring(1, coverType.length()).toLowerCase();
					if (checkType.equals("dwg") || checkType.equals("dxf") || checkType.equals("gif") || checkType.equals("jp2") || checkType.equals("jpe") || checkType.equals("jpeg") || checkType.equals("jpg") || checkType.equals("png") || checkType.equals("svf") || checkType.equals("tif") || checkType.equals("tiff")) {
						String coverid = blog.getCoverid();
						coverName = coverid + coverType;
						String coverPath = uploadPath + "/cover/" + coverName;
						System.out.println("coverPath:" + coverPath);
						File file = new File(coverPath);
						try {
							setcover.transferTo(file);
						} catch (IOException e) {
							return ResponseEntity.ok().body(new Response(false, "Upload failed"));
						}
						blog.setCover(coverName);
					}else {
						return ResponseEntity.ok().body(new Response(false, "file's type must be image"));
					}
				}

				blogService.saveBlog(blog);
			}

		} catch (ConstraintViolationException e) {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/user/" + username + "/blogs/" + blog.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

	@GetMapping("/{username}/questions/{id}")
	public String getQuestionById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {

		User principal = null;
		Question question = questionService.getQuestionById(id);

		Page<EsQuestion> page = null;
		List<EsQuestion> list = null;

		Sort sort = new Sort(Direction.DESC, "viewSize", "qcommentSize", "qvoteSize", "createTime");
		Pageable pageable = new PageRequest(0, 10, sort);
		page = esQuestionService.listHotestEsQuestions(question.getTags(), pageable);

		list = page.getContent();

		// 每次读取，简单的可以认为阅读量增加1次
		questionService.viewingIncrease(id);

		// 判断操作用户是否是博客的所有者
		boolean isQuestionOwner = false;
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !SecurityContextHolder
						.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal != null && username.equals(principal.getUsername())) {
				isQuestionOwner = true;
			}
		}

		// 判断操作用户的点赞情况
		List<QVote> qvotes = question.getQVotes();
		QVote currentQVote = null; // 当前用户的点赞情况

		if (principal != null) {
			for (QVote qvote : qvotes) {
				if (qvote.getUser().getUsername().equals(principal.getUsername())) {
					currentQVote = qvote;
					break;
				}
			}
		}

		model.addAttribute("isQuestionOwner", isQuestionOwner);
		model.addAttribute("questionModel", question);
		model.addAttribute("currentQVote", currentQVote);
		model.addAttribute("questionList", list);

		return "/detail/question";
	}

	@DeleteMapping("/{username}/questions/{id}")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> deleteQuestion(@PathVariable("username") String username,
			@PathVariable("id") Long id) {

		try {
			questionService.removeQuestion(id);
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/user/" + username;
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

	@GetMapping("/{username}/questions/edit")
	public ModelAndView createQuestion(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);

		model.addAttribute("question", new Question(null, null, null));
		model.addAttribute("user", user);
		return new ModelAndView("/detail/questionedit", "questionModel", model);
	}

	@GetMapping("/{username}/questions/edit/{id}")
	public ModelAndView editQuestion(@PathVariable("username") String username, @PathVariable("id") Long id,
			Model model) {
		// 获取用户分类列表
		User user = (User) userDetailsService.loadUserByUsername(username);

		model.addAttribute("question", questionService.getQuestionById(id));
		model.addAttribute("user", user);
		return new ModelAndView("/detail/questionedit", "questionModel", model);
	}

	@PostMapping("/{username}/questions/edit")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveQuestion(@PathVariable("username") String username, Question question) {

		User user = (User) userDetailsService.loadUserByUsername(username);

		try {
			// 判断是修改还是新增
			if (question.getId() != null) {
				Question orignalQuestion = questionService.getQuestionById(question.getId());
				orignalQuestion.setTitle(question.getTitle());
				orignalQuestion.setContent(question.getContent());
				orignalQuestion.setSummary(question.getSummary());
				orignalQuestion.setTags(question.getTags());

				questionService.saveQuestion(orignalQuestion);
			} else {
				question.setUser(user);
				questionService.saveQuestion(question);
			}

		} catch (ConstraintViolationException e) {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/user/" + username + "/questions/" + question.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}
	
	@GetMapping("/{username}/resources/{id}")
	public String getResourceById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {

		User principal = null;
		Resource resource = resourceService.getResourceById(id);

		Page<EsResource> page = null;
		List<EsResource> list = null;

		Sort sort = new Sort(Direction.DESC, "rviewSize", "rcommentSize", "rvoteSize", "createTime");
		Pageable pageable = new PageRequest(0, 3, sort);
		page = esResourceService.listHotestEsResources(resource.getTags(), pageable);

		list = page.getContent();

		// 每次读取，简单的可以认为阅读量增加1次
		resourceService.rviewingIncrease(id);

		// 判断操作用户是否是博客的所有者
		boolean isResourceOwner = false;
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !SecurityContextHolder
						.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal != null && username.equals(principal.getUsername())) {
				isResourceOwner = true;
			}
		}

		// 判断操作用户的点赞情况
		List<RVote> rvotes = resource.getRvotes();
		RVote currentRVote = null; // 当前用户的点赞情况

		if (principal != null) {
			for (RVote rvote : rvotes) {
				rvote.getUser().getUsername().equals(principal.getUsername());
				currentRVote = rvote;
				break;
			}
		}

		model.addAttribute("isResourceOwner", isResourceOwner);
		model.addAttribute("resourceModel", resource);
		model.addAttribute("currentRVote", currentRVote);
		model.addAttribute("resourceList", list);

		return "/detail/resource";
	}

	@DeleteMapping("/{username}/resources/{id}")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> deleteResource(@PathVariable("username") String username, @PathVariable("id") Long id) {

		try {
			resourceService.removeResource(id);
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/user/" + username;
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

	@GetMapping("/{username}/resources/edit")
	public ModelAndView createResource(@PathVariable("username") String username, Model model) {
		User user = (User) userDetailsService.loadUserByUsername(username);

		model.addAttribute("resource", new Resource(null, null, null));
		model.addAttribute("user", user);
		return new ModelAndView("/detail/resourceedit", "resourceModel", model);
	}

	@GetMapping("/{username}/resources/edit/{id}")
	public ModelAndView editResource(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
		// 获取用户分类列表
		User user = (User) userDetailsService.loadUserByUsername(username);

		model.addAttribute("resource", resourceService.getResourceById(id));
		model.addAttribute("user", user);
		return new ModelAndView("/detail/resourceedit", "resourceModel", model);
	}

	@PostMapping("/{username}/resources/edit")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveResource(@PathVariable("username") String username, Resource resource) {

		User user = (User) userDetailsService.loadUserByUsername(username);

		try {
			// 判断是修改还是新增
			if (resource.getId() != null) {
				Resource orignalResource = resourceService.getResourceById(resource.getId());

				orignalResource.setTitle(resource.getTitle());
				orignalResource.setContent(resource.getContent());
				orignalResource.setSummary(resource.getSummary());
				orignalResource.setTags(resource.getTags());

				resourceService.saveResource(orignalResource);
			} else {
				resource.setUser(user);

				resourceService.saveResource(resource);
			}

		} catch (ConstraintViolationException e) {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}

		String redirectUrl = "/user/" + username + "/resources/" + resource.getId();
		return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
	}

}
