package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.PageService;

@Component
public class ItemPageListener implements MessageListener {

	@Autowired
	private PageService pageService;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			String text = textMessage.getText();
			System.out.println("开始：");
			System.out.println(text);
			pageService.addItemHtml(Long.valueOf(text));
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
