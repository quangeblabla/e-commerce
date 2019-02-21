package com.pinyougou.search.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;

@Component
public class ItemSearchListener implements MessageListener{

	@Autowired
	private SearchService searchService;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			String jsonItemList = textMessage.getText();
			List<TbItem> itemList = JSON.parseArray(jsonItemList,TbItem.class);
			
			searchService.importItem(itemList);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	
}
