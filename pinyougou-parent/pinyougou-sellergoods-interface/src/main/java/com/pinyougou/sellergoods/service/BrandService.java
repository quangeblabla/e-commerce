package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import vo.PageResult;

/**
 * 
 * <p>Decription: 品牌接口</p>
 * @author java小白
 * @date 2018年9月11日下午1:04:46
 */
public interface BrandService {
	//查询所有品牌
	public List<TbBrand> findAll();
	//分页查询所有品牌
	public PageResult findAllPageBrand(int pageNum, int pageSize);
	//新增品牌
	public void addBrand(TbBrand tbBrand);
	//修改品牌
	public void update(TbBrand tbBrand);
	//根据id查询品牌
	public TbBrand findone(long id);
	//删除品牌
	public void delete(long[] ids);
	//条件查询
	public PageResult findByConditions(TbBrand tbBrand,int pageNum, int pageSize);
	//查询品牌
	public List<Map> selectOptionList();
 }
