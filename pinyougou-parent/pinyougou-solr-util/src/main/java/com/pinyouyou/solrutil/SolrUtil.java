package com.pinyouyou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private SolrTemplate solrTemplate;
	
	public void tbItemImport() {
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> list = itemMapper.selectByExample(example);
		for(TbItem item : list) {
			
		    Map specMap = JSON.parseObject(item.getSpec(),Map.class); 
			item.setSpecMap(specMap);
		}
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		System.out.println("导入成功");
	}
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
		solrUtil.tbItemImport();
//		solrUtil.deleteItem();
	}
	private void deleteItem() {
			
		Query query = new SimpleQuery("*:*");
		solrTemplate.delete(query );
		solrTemplate.commit();
		System.out.println("删除成功");
	} 
}
