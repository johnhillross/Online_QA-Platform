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
import com.spring.boot.domain.es.EsQuestion;
import com.spring.boot.repository.es.EsQuestionRepository;
import com.spring.boot.vo.TagVO;

@Service
public class EsQuestionServiceImpl implements EsQuestionService {
	
	@Autowired
	private EsQuestionRepository esQuestionRepository;
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private UserService userService;

	private static final Pageable TOP_5_PAGEABLE = new PageRequest(0, 5);
	private static final String EMPTY_KEYWORD = "";
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.question.service.EsQuestionService#removeEsQuestion(java.lang.String)
	 */
	@Override
	public void removeEsQuestion(String id) {
		esQuestionRepository.delete(id);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.question.service.EsQuestionService#updateEsQuestion(com.waylau.spring.boot.question.domain.es.EsQuestion)
	 */
	@Override
	public EsQuestion updateEsQuestion(EsQuestion esQuestion) {
		return esQuestionRepository.save(esQuestion);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.question.service.EsQuestionService#getEsQuestionByQuestionId(java.lang.Long)
	 */
	@Override
	public EsQuestion getEsQuestionByQuestionId(Long questionId) {
		return esQuestionRepository.findByQuestionId(questionId);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.question.service.EsQuestionService#listNewestEsQuestions(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<EsQuestion> listNewestEsQuestions(String keyword, Pageable pageable) throws SearchParseException {
		
		Sort sort = new Sort(Direction.DESC,"createTime"); 
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		return esQuestionRepository.findDistinctEsQuestionByTitleContainingOrSummaryContainingOrContentContaining(keyword,keyword,keyword, pageable);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.question.service.EsQuestionService#listHotestEsQuestions(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<EsQuestion> listHotestEsQuestions(String keyword, Pageable pageable) throws SearchParseException{
 
		Sort sort = new Sort(Direction.DESC,"answerSize","viewSize","qcommentSize","qvoteSize","createTime"); 
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		return esQuestionRepository.findDistinctEsQuestionByTitleContainingOrSummaryContainingOrContentContaining(keyword, keyword, keyword, pageable);
	}

	@Override
	public Page<EsQuestion> listEsQuestions(Pageable pageable) {
		return esQuestionRepository.findAll(pageable);
	}
 
 
	/**
	 * 最新前5
	 * @param keyword
	 * @return
	 */
	@Override
	public List<EsQuestion> listTop5NewestEsQuestions() {
		Page<EsQuestion> page = this.listNewestEsQuestions(EMPTY_KEYWORD, TOP_5_PAGEABLE);
		return page.getContent();
	}
	
	/**
	 * 最热前5
	 * @param keyword
	 * @return
	 */
	@Override
	public List<EsQuestion> listTop5HotestEsQuestions() {
		Page<EsQuestion> page = this.listHotestEsQuestions(EMPTY_KEYWORD, TOP_5_PAGEABLE);
		return page.getContent();
	}

	@Override
	public List<TagVO> listTop30Tags() {
 
		List<TagVO> list = new ArrayList<>();
		// given
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withSearchType(SearchType.QUERY_THEN_FETCH)
				.withIndices("question").withTypes("question")
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
				.withIndices("question").withTypes("question")
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
