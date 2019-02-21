package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
	
	public Map search(Map searchMaps);
	
	public void importItem(List itemList);
}
