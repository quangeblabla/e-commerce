package com.pinyougou.page.service.impl;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;
@Service
public class PageServiceImpl implements PageService {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Value("${pagedir}")
	private String pagedir;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public void addItemHtml(Long goodsId) {
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		try {
			//获取模板
			Template template = configuration.getTemplate("item.ftl");
			//数据源
			Map dataModel = new HashMap();
			
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			
			//分类名称
			String category1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String category2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String category3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			
			dataModel.put("category1Name", category1Name);
			dataModel.put("category2Name", category2Name);
			dataModel.put("category3Name", category3Name);
			dataModel.put("goods", goods);
			dataModel.put("goodsDesc", goodsDesc);
			dataModel.put("itemList", itemList);
			//输出位置
			Writer out = new FileWriter(pagedir+goodsId+".html");
			//执行
			template.process(dataModel, out);
			//关流
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
