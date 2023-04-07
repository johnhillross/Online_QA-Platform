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

@Entity
@Document(indexName = "answer", type = "answer")
public class Answer implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识
	
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
	
	@Column(name="acommentSize")
	private Integer acommentSize = 0;  // 回答量
	
	@Column(name="avoteSize")
	private Integer avoteSize = 0;  // 点赞量
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "answer_acomment", joinColumns = @JoinColumn(name = "answer_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "acomment_id", referencedColumnName = "id"))
	private List<AComment> acomments;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "answer_avote", joinColumns = @JoinColumn(name = "answer_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "avote_id", referencedColumnName = "id"))
	private List<AVote> avotes;

	protected Answer() {
		// TODO Auto-generated constructor stub
	}
	
	public Answer(User user, String content) {
		this.user = user;
		this.content = content;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public Integer getAcommentSize() {
		return acommentSize;
	}
	public void setAcommentSize(Integer acommentSize) {
		this.acommentSize = acommentSize;
	}
	public Integer getAvoteSize() {
		return avoteSize;
	}
	public void setAvoteSize(Integer avoteSize) {
		this.avoteSize = avoteSize;
	}
	public List<AComment> getAComments() {
		return acomments;
	}
	public void setAComments(List<AComment> acomments) {
		this.acomments = acomments;
		this.acommentSize = this.acomments.size();
	}
	
	/**
	 * 添加回答
	 * @param acomment
	 */
	public void addAComment(AComment acomment) {
		this.acomments.add(acomment);
		this.acommentSize = this.acomments.size();
	}
	/**
	 * 删除回答
	 * @param acomment
	 */
	public void removeAComment(Long acommentId) {
		for (int index=0; index < this.acomments.size(); index ++ ) {
			if (acomments.get(index).getId() == acommentId) {
				this.acomments.remove(index);
				break;
			}
		}
		
		this.acommentSize = this.acomments.size();
	}
 
	/**
	 * 点赞
	 * @param avote
	 * @return
	 */
	public boolean addAUpVote(AVote avote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.avotes.size(); index ++ ) {
			if (this.avotes.get(index).getUser().getId() == avote.getUser().getId() && this.avotes.get(index).isUpdown()) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			avote.setUpdown(true);
			this.avotes.add(avote);
			
			countVote();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param avoteId
	 */
	public void removeAUpVote(Long avoteId) {
		for (int index=0; index < this.avotes.size(); index ++ ) {
			if (this.avotes.get(index).getId() == avoteId && this.avotes.get(index).isUpdown()) {
				this.avotes.remove(index);
				break;
			}
		}
		
		countVote();
	}
	
	/**
	 * 点赞
	 * @param avote
	 * @return
	 */
	public boolean addADownVote(AVote avote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.avotes.size(); index ++ ) {
			if (this.avotes.get(index).getUser().getId() == avote.getUser().getId() && (!this.avotes.get(index).isUpdown())) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			avote.setUpdown(false);
			this.avotes.add(avote);
			
			countVote();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param avoteId
	 */
	public void removeADownVote(Long avoteId) {
		for (int index=0; index < this.avotes.size(); index ++ ) {
			if (this.avotes.get(index).getId() == avoteId && (!this.avotes.get(index).isUpdown())) {
				this.avotes.remove(index);
				break;
			}
		}
		
		countVote();
	}
	
	public void countVote() {
		int upvote = 0;
		int downvote = 0;
		for (int index=0; index < this.avotes.size(); index ++ ) {
			if (this.avotes.get(index).isUpdown()) {
				upvote++;
			} else {
				downvote++;
			}
		}
		
		this.avoteSize = upvote - downvote;
	}
	
	public List<AVote> getAVotes() {
		return avotes;
	}
	public void setAVotes(List<AVote> avotes) {
		this.avotes = avotes;
		countVote();
	}
}
