package com.spring.boot.domain.es;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

import com.spring.boot.domain.Resource;

@Document(indexName = "resource", type = "resource")
@XmlRootElement // MediaType 转为 XML
public class EsResource implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id  // 主键
	private String id;  

	@Field(index = FieldIndex.not_analyzed)    
	private Long resourceId; // Resource 的 id
	
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
	private Integer rviewSize = 0; // 访问量、阅读量
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Integer rcommentSize = 0;  // 评论量
	@Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段  
	private Integer rvoteSize = 0;  // 点赞量
	private String tags;  // 标签
	private String imageList;	//图片列表
	
	protected EsResource() {  // JPA 的规范要求无参构造函数；设为 protected 防止直接使用 
	}
	
	public EsResource(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	public EsResource(Long resourceId, String title, String summary, String content, String username, String avatar, Timestamp createTime,
			Integer rviewSize,Integer rcommentSize, Integer rvoteSize , String tags, String imageList) {
		this.resourceId = resourceId;
		this.title = title;
		this.summary = summary;
		this.content = content;
		this.username = username;
		this.avatar = avatar;
		this.createTime = createTime;
		this.rviewSize = rviewSize;
		this.rcommentSize = rcommentSize;
		this.rvoteSize = rvoteSize;
		this.tags = tags;
		this.imageList = imageList;
	}
	
	public EsResource(Resource resource){
		this.resourceId = resource.getId();
		this.title = resource.getTitle();
		this.summary = resource.getSummary();
		this.content = resource.getContent();
		this.username = resource.getUser().getUsername();
		this.avatar = resource.getUser().getAvatar();
		this.createTime = resource.getCreateTime();
		this.rviewSize = resource.getRviewSize();
		this.rcommentSize = resource.getRcommentSize();
		this.rvoteSize = resource.getRvoteSize();
		this.tags = resource.getTags();
		this.imageList = resource.getImageList();
	}
	
	public void update(Resource resource){
		this.resourceId = resource.getId();
		this.title = resource.getTitle();
		this.summary = resource.getSummary();
		this.content = resource.getContent();
		this.username = resource.getUser().getUsername();
		this.avatar = resource.getUser().getAvatar();
		this.createTime = resource.getCreateTime();
		this.rviewSize = resource.getRviewSize();
		this.rcommentSize = resource.getRcommentSize();
		this.rvoteSize = resource.getRvoteSize();
		this.tags = resource.getTags();
		this.imageList = resource.getImageList();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getImageList() {
		return imageList;
	}

	public void setImageList(String imageList) {
		this.imageList = imageList;
	}
	
	@Override
    public String toString() {
        return String.format(
                "User[id=%d, title='%s', content='%s']",
                resourceId, title, content);
    }
}
