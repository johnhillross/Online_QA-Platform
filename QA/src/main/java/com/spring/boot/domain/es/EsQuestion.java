package com.spring.boot.domain.es;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

import com.spring.boot.domain.Question;


@Document(indexName = "question", type = "question")
@XmlRootElement // MediaType 转为 XML
public class EsQuestion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id  // 主键
	private String id;  
	@Field(index = FieldIndex.not_analyzed)    
	private Long questionId; // Question 的 id
 
	private String title;
 
	private String summary;
	
	private String content;
	 
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private String username;
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private String avatar;
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Timestamp createTime;
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Integer viewSize = 0; // 访问量、阅读量
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Integer answerSize = 0;  // 评论量
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Integer qvoteSize = 0;  // 点赞量
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Integer qcommentSize = 0;  // 点赞量
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Long accAnswerId = 0L;  // 点赞量
	private boolean accState = false;

	private String tags;  // 标签
	
	protected EsQuestion() {  // JPA 的规范要求无参构造函数；设为 protected 防止直接使用 
	}

	public EsQuestion(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	public EsQuestion(Long questionId, String title, String summary, String content, String username, String avatar,Timestamp createTime,
			Integer viewSize,Integer answerSize, Integer qvoteSize , Integer qcommentSize, Long accAnswerId, boolean accState, String tags) {
		this.questionId = questionId;
		this.title = title;
		this.summary = summary;
		this.content = content;
		this.username = username;
		this.avatar = avatar;
		this.createTime = createTime;
		this.viewSize = viewSize;
		this.answerSize = answerSize;
		this.qvoteSize = qvoteSize;
		this.qcommentSize = qcommentSize;
		this.accAnswerId = accAnswerId;
		this.accState = accState;
		this.tags = tags;
	}
	
	public EsQuestion(Question question){
		this.questionId = question.getId();
		this.title = question.getTitle();
		this.summary = question.getSummary();
		this.content = question.getContent();
		this.username = question.getUser().getUsername();
		this.avatar = question.getUser().getAvatar();
		this.createTime = question.getCreateTime();
		this.viewSize = question.getViewSize();
		this.answerSize = question.getAnswerSize();
		this.qvoteSize = question.getQvoteSize();
		this.qcommentSize = question.getQcommentSize();
		this.accAnswerId = question.getAccAnswerId();
		this.accState = question.isAccState();
		this.tags = question.getTags();
	}
 
	public void update(Question question){
		this.questionId = question.getId();
		this.title = question.getTitle();
		this.summary = question.getSummary();
		this.content = question.getContent();
		this.username = question.getUser().getUsername();
		this.avatar = question.getUser().getAvatar();
		this.createTime = question.getCreateTime();
		this.viewSize = question.getViewSize();
		this.answerSize = question.getAnswerSize();
		this.qvoteSize = question.getQvoteSize();
		this.qcommentSize = question.getQcommentSize();
		this.accAnswerId = question.getAccAnswerId();
		this.accState = question.isAccState();
		this.tags = question.getTags();
	}
 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
 
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
 
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getViewSize() {
		return viewSize;
	}

	public void setViewSize(Integer viewSize) {
		this.viewSize = viewSize;
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
	
	public Integer getQcommentSize() {
		return qcommentSize;
	}

	public void setQcommentSize(Integer qcommentSize) {
		this.qcommentSize = qcommentSize;
	}

	public String getTags() {
		return tags;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	@Override
    public String toString() {
        return String.format(
                "User[id=%d, title='%s', content='%s']",
                questionId, title, content);
    }

}
