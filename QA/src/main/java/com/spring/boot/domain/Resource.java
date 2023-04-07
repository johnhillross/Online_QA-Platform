package com.spring.boot.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.elasticsearch.annotations.Document;

import com.spring.boot.domain.RComment;
import com.spring.boot.domain.User;
import com.spring.boot.domain.RVote;

@Entity
@Document(indexName = "resource", type = "resource")
public class Resource implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识

	@NotEmpty(message = "标题不能为空")
	@Size(min=2, max=50)
	@Column(nullable = false, length = 50) // 映射为字段，值不能为空
	private String title;
	
	@NotEmpty(message = "摘要不能为空")
	@Size(min=2, max=300)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String summary;
	
	@Lob  // 大对象，映射 MySQL 的 Long Text 类型
	@Basic(fetch=FetchType.LAZY) // 懒加载
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String content;
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(nullable = false) // 映射为字段，值不能为空
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;
	
	@Column(name="rviewSize")
	private Integer rviewSize = 0; // 访问量、阅读量
	
	@Column(name="rcommentSize")
	private Integer rcommentSize = 0;  // 评论量
	
	@Column(name="rvoteSize")
	private Integer rvoteSize = 0;  // 点赞量
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "resource_rcomment", joinColumns = @JoinColumn(name = "resource_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "rcomment_id", referencedColumnName = "id"))
	private List<RComment> rcomments;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "resource_rvote", joinColumns = @JoinColumn(name = "resource_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "rvote_id", referencedColumnName = "id"))
	private List<RVote> rvotes;
	
	@Column(name="tags", length = 100) 
	private String tags;  // 标签
	
	@NotEmpty(message = "内容不能为空")
	@Column(name="fileList") 
	private String fileList;  // 标签
	
	@Column(name="imageList") 
	private String imageList;  // 标签
	
	public Resource(String title, String summary,String content) {
		this.title = title;
		this.summary = summary;
		this.content = content;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getRviewSize() {
		return rviewSize;
	}

	public void setRviewSize(Integer rviewSize) {
		this.rviewSize = rviewSize;
	}

	public Integer getRcommentSize() {
		return rcommentSize;
	}

	public void setRcommentSize(Integer rcommentSize) {
		this.rcommentSize = rcommentSize;
	}

	public Integer getRvoteSize() {
		return rvoteSize;
	}

	public void setRvoteSize(Integer rvoteSize) {
		this.rvoteSize = rvoteSize;
	}
	
	public List<RComment> getRcomments() {
		return rcomments;
	}

	public void setRcomments(List<RComment> rcomments) {
		this.rcomments = rcomments;
		this.rcommentSize = this.rcomments.size();
	}
	
	/**
	 * 添加评论
	 * @param comment
	 */
	public void addRComment(RComment rcomment) {
		this.rcomments.add(rcomment);
		this.rcommentSize = this.rcomments.size();
	}
	/**
	 * 删除评论
	 * @param comment
	 */
	public void removeRComment(Long rcommentId) {
		for (int index=0; index < this.rcomments.size(); index ++ ) {
			if (rcomments.get(index).getId() == rcommentId) {
				this.rcomments.remove(index);
				break;
			}
		}
		
		this.rcommentSize = this.rcomments.size();
	}

	public List<RVote> getRvotes() {
		return rvotes;
	}

	public void setRvotes(List<RVote> rvotes) {
		this.rvotes = rvotes;
		this.rvoteSize = this.rvotes.size();
	}
	
	/**
	 * 点赞
	 * @param vote
	 * @return
	 */
	public boolean addRVote(RVote rvote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.rvotes.size(); index ++ ) {
			if (this.rvotes.get(index).getUser().getId() == rvote.getUser().getId()) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			this.rvotes.add(rvote);
			this.rvoteSize = this.rvotes.size();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param voteId
	 */
	public void removeRVote(Long rvoteId) {
		for (int index=0; index < this.rvotes.size(); index ++ ) {
			if (this.rvotes.get(index).getId() == rvoteId) {
				this.rvotes.remove(index);
				break;
			}
		}
		
		this.rvoteSize = this.rvotes.size();
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}
	
	public String getImageList() {
		return imageList;
	}

	public void setImageList(String imageList) {
		this.imageList = imageList;
	}
}
