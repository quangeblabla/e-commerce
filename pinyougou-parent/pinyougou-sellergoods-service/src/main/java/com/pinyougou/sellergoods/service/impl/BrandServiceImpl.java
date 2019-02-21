package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import vo.PageResult;
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper tbBrandMapper;
	/**
	 * 查询所有品牌
	 */
	@Override
	public List<TbBrand> findAll() {
		return tbBrandMapper.selectByExample(null);
	}
	
	/**
	 * 分页查询所有品牌
	 */
	@Override
	public PageResult findAllPageBrand(int pageNum, int pageSize) {
		
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	/**
	 * 增加品牌
	 */
	@Override
	public void addBrand(TbBrand tbBrand) {
		tbBrandMapper.insert(tbBrand);
	}
	/**
	 * 修改品牌
	 */
	@Override
	public void update(TbBrand tbBrand) {
		tbBrandMapper.updateByPrimaryKey(tbBrand);
	}
	/**
	 * 根据id查询品牌
	 */
	@Override
	public TbBrand findone(long id) {
		return tbBrandMapper.selectByPrimaryKey(id);
	}
	/**
	 * 根据id删除品牌
	 */
	@Override
	public void delete(long[] ids) {
		for (long id : ids) {
			tbBrandMapper.deleteByPrimaryKey(id);
		}
	}
	/**
	 * 条件分页查询
	 */
	@Override
	public PageResult findByConditions(TbBrand tbBrand, int pageNum, int pageSize) {
		//分页
		PageHelper.startPage(pageNum, pageSize);
		//条件
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if(tbBrand != null) {
			if(tbBrand.getName() != null && tbBrand.getName().length() > 0) {
				criteria.andNameLike("%"+tbBrand.getName()+"%");
			}
		}
		if(tbBrand.getFirstChar() != null && tbBrand.getFirstChar() != "") {
			criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
		}
		Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return tbBrandMapper.selectOptionList();
	}

}
