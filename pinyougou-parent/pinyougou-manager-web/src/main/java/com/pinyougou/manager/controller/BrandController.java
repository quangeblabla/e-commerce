package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import vo.PageResult;
import vo.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandService brandService;
	/**
	 * 查询所有品牌
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	/**
	 * 分页查询所有品牌
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/findAllPageBrand")
	public PageResult findAllPageBrand(int pageNum, int pageSize ) {
		return brandService.findAllPageBrand(pageNum, pageSize);
	}
	/**
	 * 新增品牌
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/add")
	public Result addBrand(@RequestBody TbBrand tbBrand) {
		try {
			brandService.addBrand(tbBrand);
			return new Result(true,"增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"增加失败");
		}
	}
	/**
	 * 根据id查询品牌
	 * @param id
	 * @return
	 */
	@RequestMapping("/findone")
	public TbBrand findone(long id) {
		return brandService.findone(id);
	}
	/**
	 * 修改品牌
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand){
		try {
			brandService.update(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	/**
	 * 删除
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(long[] ids){
		try {
			brandService.delete(ids);;
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	@RequestMapping("/findConditions")
	public PageResult findByconditions(@RequestBody TbBrand tbBrand, int pageNum, int pageSize) {
		PageResult pageResult = brandService.findByConditions(tbBrand, pageNum, pageSize);
		return pageResult;
	}
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
	
	
	
	
	
	
	
	
}
