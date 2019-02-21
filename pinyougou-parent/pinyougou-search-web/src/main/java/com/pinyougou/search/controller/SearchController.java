package com.pinyougou.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;

@RestController
public class SearchController {
	@Reference
	private SearchService searchService;
	
	@RequestMapping("/search")
	public Map itemSearch(@RequestBody Map searchMap){
		return searchService.search(searchMap);
	}
}
