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
@Document(indexName = "question", type = "question")
public class Question implements Serializable {
	
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
	
	@Column(name="viewize")
	private Integer viewSize = 0; // 浏览量
	
	@Column(name="qcommentSize")
	private Integer qcommentSize = 0;  // 评论量
	
	@Column(name="answerSize")
	private Integer answerSize = 0;  // 回答量
	
	@Column(name="qvoteSize")
	private Integer qvoteSize = 0;  // 点赞量
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "question_qcomment", joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "qcomment_id", referencedColumnName = "id"))
	private List<QComment> qcomments;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "question_answer", joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "answer_id", referencedColumnName = "id"))
	private List<Answer> answers;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "question_qvote", joinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "qvote_id", referencedColumnName = "id"))
	private List<QVote> qvotes;

	@Column(name="tags", length = 100) 
	private String tags;  // 标签
	
	private Long accAnswerId = 0L;
	
	private boolean accState = false;

	protected Question() {
		// TODO Auto-generated constructor stub
	}
	
	public Question(String title, String summary,String content) {
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
	
	public Integer getViewSize() {
		return viewSize;
	}
	public void setViewSize(Integer viewSize) {
		this.viewSize = viewSize;
	}
	public Integer getQcommentSize() {
		return qcommentSize;
	}
	public void setQcommentSize(Integer qcommentSize) {
		this.qcommentSize = qcommentSize;
	}
	public Integer getAnswerSize() {
		return answerSize;
	}
	public void setAnswerSize(Integer answerSize) {
		this.answerSize = answerSize;
	}
	public Integer getQvoteSize() {
		return qvoteSize;
	}
	public void setQvoteSize(Integer qvoteSize) {
		this.qvoteSize = qvoteSize;
	}
	public List<QComment> getQComments() {
		return qcomments;
	}
	public void setQComments(List<QComment> qcomments) {
		this.qcomments = qcomments;
		this.qcommentSize = this.qcomments.size();
	}
	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
		this.answerSize = this.answers.size();
	}
	
	/**
	 * 添加评论
	 * @param qcomment
	 */
	public void addQComment(QComment qcomment) {
		this.qcomments.add(qcomment);
		this.qcommentSize = this.qcomments.size();
	}
	/**
	 * 删除评论
	 * @param qcomment
	 */
	public void removeQComment(Long qcommentId) {
		for (int index=0; index < this.qcomments.size(); index ++ ) {
			if (qcomments.get(index).getId() == qcommentId) {
				this.qcomments.remove(index);
				break;
			}
		}
		
		this.qcommentSize = this.qcomments.size();
	}
	
	/**
	 * 添加回答
	 * @param answer
	 */
	public void addAnswer(Answer answer) {
		this.answers.add(answer);
		this.answerSize = this.answers.size();
	}
	/**
	 * 删除回答
	 * @param answer
	 */
	public void removeAnswer(Long answerId) {
		for (int index=0; index < this.answers.size(); index ++ ) {
			if (answers.get(index).getId() == answerId) {
				this.answers.remove(index);
				break;
			}
		}
		
		this.answerSize = this.answers.size();
	}
 
	/**
	 * 点赞
	 * @param qvote
	 * @return
	 */
	public boolean addQUpVote(QVote qvote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.qvotes.size(); index ++ ) {
			if (this.qvotes.get(index).getUser().getId() == qvote.getUser().getId() && this.qvotes.get(index).isUpdown()) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			qvote.setUpdown(true);
			this.qvotes.add(qvote);
			
			countVote();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param qvoteId
	 */
	public void removeQUpVote(Long qvoteId) {
		for (int index=0; index < this.qvotes.size(); index ++ ) {
			if (this.qvotes.get(index).getId() == qvoteId && this.qvotes.get(index).isUpdown()) {
				this.qvotes.remove(index);
				break;
			}
		}
		
		countVote();
	}
	
	/**
	 * 点赞
	 * @param qvote
	 * @return
	 */
	public boolean addQDownVote(QVote qvote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.qvotes.size(); index ++ ) {
			if (this.qvotes.get(index).getUser().getId() == qvote.getUser().getId() && (!this.qvotes.get(index).isUpdown())) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			qvote.setUpdown(false);
			this.qvotes.add(qvote);
			
			countVote();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param qvoteId
	 */
	public void removeQDownVote(Long qvoteId) {
		for (int index=0; index < this.qvotes.size(); index ++ ) {
			if (this.qvotes.get(index).getId() == qvoteId && (!this.qvotes.get(index).isUpdown())) {
				this.qvotes.remove(index);
				break;
			}
		}
		
		countVote();
	}
	
	public void countVote() {
		int upvote = 0;
		int downvote = 0;
		for (int index=0; index < this.qvotes.size(); index ++ ) {
			if (this.qvotes.get(index).isUpdown()) {
				upvote++;
			} else {
				downvote++;
			}
		}
		
		this.qvoteSize = upvote - downvote;
	}
	
	public List<QVote> getQVotes() {
		return qvotes;
	}
	public void setQVotes(List<QVote> qvotes) {
		this.qvotes = qvotes;
		countVote();
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public boolean isAccState() {
		return accState;
	}

	public void setAccState(boolean accState) {
		this.accState = accState;
	}
	
	public Long getAccAnswerId() {
		return accAnswerId;
	}

	public void setAccAnswerId(Long accAnswerId) {
		this.accAnswerId = accAnswerId;
		if (accAnswerId != 0) {
			setAccState(true);
		}else {
			setAccState(false);
		}
	}

}
