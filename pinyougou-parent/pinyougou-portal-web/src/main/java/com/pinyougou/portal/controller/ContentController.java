package com.pinyougou.portal.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

import vo.PageResult;
import vo.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findByCategoryId")
	public List<TbContent> findByCategoryId(long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
	
}
