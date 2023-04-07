package com.spring.boot.service;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.SearchParseException;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.User;
import com.spring.boot.domain.es.EsResource;
import com.spring.boot.repository.es.EsResourceRepository;
import com.spring.boot.vo.TagVO;

@Service
public class EsResourceServiceImpl implements EsResourceService {

	@Autowired
	private EsResourceRepository esResourceRepository;
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private UserService userService;
	
	private static final Pageable TOP_5_PAGEABLE = new PageRequest(0, 5);
	private static final String EMPTY_KEYWORD = "";
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.resource.service.EsResourceService#removeEsResource(java.lang.String)
	 */
	@Override
	public void removeEsResource(String id) {
		esResourceRepository.delete(id);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.resource.service.EsResourceService#updateEsResource(com.waylau.spring.boot.resource.domain.es.EsResource)
	 */
	@Override
	public EsResource updateEsResource(EsResource esResource) {
		return esResourceRepository.save(esResource);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.resource.service.EsResourceService#getEsResourceByResourceId(java.lang.Long)
	 */
	@Override
	public EsResource getEsResourceByResourceId(Long resourceId) {
		return esResourceRepository.findByResourceId(resourceId);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.resource.service.EsResourceService#listNewestEsResources(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<EsResource> listNewestEsResources(String keyword, Pageable pageable) throws SearchParseException {
		Page<EsResource> pages = null;
		Sort sort = new Sort(Direction.DESC,"createTime"); 
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		pages = esResourceRepository.findDistinctEsResourceByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(keyword,keyword,keyword,keyword, pageable);
 
		return pages;
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.resource.service.EsResourceService#listHotestEsResources(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<EsResource> listHotestEsResources(String keyword, Pageable pageable) throws SearchParseException{
 
		Sort sort = new Sort(Direction.DESC,"readSize","commentSize","voteSize","createTime"); 
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		return esResourceRepository.findDistinctEsResourceByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(keyword, keyword, keyword, keyword, pageable);
	}

	@Override
	public Page<EsResource> listEsResources(Pageable pageable) {
		return esResourceRepository.findAll(pageable);
	}
 
 
	/**
	 * 最新前5
	 * @param keyword
	 * @return
	 */
	@Override
	public List<EsResource> listTop5NewestEsResources() {
		Page<EsResource> page = this.listHotestEsResources(EMPTY_KEYWORD, TOP_5_PAGEABLE);
		return page.getContent();
	}
	
	/**
	 * 最热前5
	 * @param keyword
	 * @return
	 */
	@Override
	public List<EsResource> listTop5HotestEsResources() {
		Page<EsResource> page = this.listHotestEsResources(EMPTY_KEYWORD, TOP_5_PAGEABLE);
		return page.getContent();
	}

	@Override
	public List<TagVO> listTop30Tags() {
 
		List<TagVO> list = new ArrayList<>();
		// given
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withSearchType(SearchType.QUERY_THEN_FETCH)
				.withIndices("resource").withTypes("resource")
				.addAggregation(terms("tags").field("tags").order(Terms.Order.count(false)).size(30))
				.build();
		// when
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		
		StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("tags"); 
	        
        Iterator<Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();
 
            list.add(new TagVO(actiontypeBucket.getKey().toString(),
                    actiontypeBucket.getDocCount()));
        }
		return list;
	}
	
	@Override
	public List<User> listTop12Users() {
 
		List<String> usernamelist = new ArrayList<>();
		// given
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withSearchType(SearchType.QUERY_THEN_FETCH)
				.withIndices("resource").withTypes("resource")
				.addAggregation(terms("users").field("username").order(Terms.Order.count(false)).size(12))
				.build();
		// when
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		
		StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("users"); 
	        
        Iterator<Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();
            String username = actiontypeBucket.getKey().toString();
            usernamelist.add(username);
        }
        List<User> list = userService.listUsersByUsernames(usernamelist);
        
		return list;
	}

}
