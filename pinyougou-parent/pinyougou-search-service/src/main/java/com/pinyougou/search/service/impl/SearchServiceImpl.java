package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;

@Service(timeout=5000)
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	
	/**
	 * 主线
	 */
	@Override
	public Map search(Map searchMap) {
		//去除关键字空格
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replaceAll(" ", ""));
		Map map = new HashMap();
		//获取关键字查询后结果
		map.putAll(searchList(searchMap));
		//获取分类信息
		List categoryList = searchCategoryList(searchMap);
		map.put("category", categoryList);
		//查询品牌和分类列表
		if(!"".equals(searchMap.get("category"))) {
			Map brandAndSpecMap = searchBrandAndSpecList((String)searchMap.get("category"));
			map.putAll(brandAndSpecMap);
		}else {
			if(categoryList.size()>0) {
				Map brandAndSpecMap = searchBrandAndSpecList((String)categoryList.get(0));
				map.putAll(brandAndSpecMap);
			}
		}
		
		return map;
	}
	/**
	 * 基本查询
	 * @param searchMap
	 * @return
	 */
	private Map searchList(Map searchMap) {
		
		Map map = new HashMap();
		
		//高亮查询
		HighlightQuery query = new SimpleHighlightQuery();
		//获取高亮选项对象
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		//设置高亮的前后样式
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		highlightOptions.setSimplePostfix("</em>");
		//查询对象设置高亮选项
		query.setHighlightOptions(highlightOptions);
		
		
		//关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//过滤查询
		filterQuery(query,searchMap);
		
		//分页
		Integer pageCurrent = (Integer) searchMap.get("pageCurrent");//当前页
		Integer pageSize = (Integer) searchMap.get("pageSize");//当前页面数量
		if(pageCurrent == null) { pageCurrent =1;}
		if(pageSize == null) {pageSize = 20;}
		query.setOffset((pageCurrent-1)*pageSize);
		query.setRows(pageSize);
		
		//排序
		sortSearch(query,searchMap);
		
		
		//高亮page
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query , TbItem.class);
		//高亮入口(每条记录)
		List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : highlighted) {
			//高亮列表(高亮域)
			List<Highlight> highlights = highlightEntry.getHighlights();
			for (Highlight highlight : highlights) {
				//域可能存储多值
				List<String> snipplets = highlight.getSnipplets();
				if(snipplets.size()>0) {
					TbItem tbItem = highlightEntry.getEntity();
					tbItem.setTitle(snipplets.get(0));
				}
			}
		}
		map.put("rows", highlightPage.getContent());
		map.put("pageTotal", highlightPage.getTotalPages());
		map.put("total", highlightPage.getTotalElements());
		return map;
	}
	/**
	 * 排序
	 * @param query
	 * @param searchMap
	 */
	private void sortSearch(Query query,Map searchMap) {
		//获取排序排序字段和排序顺序
		String sortValue = (String) searchMap.get("sortValue");
		String sortField = (String) searchMap.get("sortField");

		if(sortValue!=null && !"".equals(sortValue) && sortField!=null && !"".equals(sortField)) {
			if("ASC".equals(sortValue)) {
				Sort sort = new Sort(Direction.ASC,"item_"+sortField );
				query.addSort(sort );
			}
			if("DESC".equals(sortValue)) {
				Sort sort = new Sort(Direction.DESC,"item_"+sortField );
				query.addSort(sort );
			}
		}
	}
	
	/**
	 * 过滤查询
	 * @param query
	 * @param searchMap
	 */
	private void filterQuery(Query query,Map searchMap) {
		//分类过滤查询
		if(!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(filterCriteria );
			query.addFilterQuery(filterQuery);
		}
		
		//品牌过滤查询
		if(!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(filterCriteria );
			query.addFilterQuery(filterQuery);
		}
		
		//规格过滤查询
		if(searchMap.get("spec") != null) {
			Map<String, String> specmap = (Map<String, String>) searchMap.get("spec");
			
			for (String key : specmap.keySet()) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specmap.get(key));
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery);
			}
			
		}
		//价格查询
		if(!"".equals(searchMap.get("price"))) {
			String[] prices = searchMap.get("price").toString().split("-");
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria criteria = new Criteria("item_price");
			if(!"*".equals(prices[1])) {
				criteria.between(prices[0], prices[1]);
			}else {
				criteria.greaterThanEqual(prices[0]);
			}
			filterQuery.addCriteria(criteria);
			query.addFilterQuery(filterQuery);
		}
	}
	
	/**
	 * 查询分类列表
	 * @param searchMap
	 * @return
	 */
	private List searchCategoryList(Map searchMap) {
		List list = new ArrayList();
		//关键字查询
		Query query = new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_keywords").in(searchMap.get("keywords"));
		query.addCriteria(criteria );
		//创建分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions );
		//分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query , TbItem.class);
		//根据域获取结果
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//获取入口
		Page<GroupEntry<TbItem>> entryList = groupResult.getGroupEntries();
		for (GroupEntry<TbItem> groupEntry : entryList) {
			list.add(groupEntry.getGroupValue());
		}
		return list;
	}
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询品牌和规格列表
	 * @param category
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap();
		//根据分类名称返回模板id
		Long templateId = (Long) redisTemplate.boundHashOps("itemcat").get(category);
		if(templateId != null) {
			//获取品牌列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
			map.put("brandList",brandList);
			//获取规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
			map.put("specList",specList);
		}
		return map;
	}
	@Override
	public void importItem(List itemList) {
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		
	}
}
