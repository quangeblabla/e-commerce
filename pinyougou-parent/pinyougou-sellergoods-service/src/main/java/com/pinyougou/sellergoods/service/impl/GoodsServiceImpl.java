package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.GoodsService;

import vo.Goods;
import vo.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper GoodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		// 插入商品基本信息
		goods.getTbGoods().setAuditStatus("0");// 商品审核状态 0未审核
		goodsMapper.insert(goods.getTbGoods());

		// 插入商品扩展信息
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());// 设置主键
		GoodsDescMapper.insert(goods.getTbGoodsDesc());

		//设置sku
		setSpec(goods);
		
	}

	private void setItem(Goods goods, TbItem tbItem) {
		// 图片绑定
		List<Map> list = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
		if (list.size() > 0) {
			tbItem.setImage((String) list.get(0).get("url"));
		}

		// 商品分类
		tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());// 三级分类

		// 创建时间和修改时间
		tbItem.setCreateTime(new Date());// 创建时间
		tbItem.setUpdateTime(new Date());// 修改时间

		// 商品Id
		tbItem.setGoodsId(goods.getTbGoods().getId());
		// 商家id
		tbItem.setSellerId(goods.getTbGoods().getSellerId());
		// 分类名称
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
		tbItem.setCategory(tbItemCat.getName());
		// 品牌名称
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		tbItem.setBrand(tbBrand.getName());
		// 店铺名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		tbItem.setSeller(tbSeller.getNickName());
	}
	/**
	 * 设置sku
	 * @param goods
	 */
	private void setSpec(Goods goods) {
		if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
			// sku信息插入
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
				// 构建标题 spu名称 + 规格选项值
				String title = goods.getTbGoods().getGoodsName();// 商品名称
				Map<String, Object> map = JSON.parseObject(tbItem.getSpec());
				for (String key : map.keySet()) {
					title += " " + map.get(key);
				}
				tbItem.setTitle(title);

				setItem(goods, tbItem);

				itemMapper.insert(tbItem);
			}
		} else {
			TbItem tbItem = new TbItem();

			tbItem.setTitle(goods.getTbGoods().getGoodsName());// 标题
			tbItem.setPrice(goods.getTbGoods().getPrice());// 价格
			tbItem.setNum(99999);// 库存
			tbItem.setStatus("1");// 状态
			tbItem.setIsDefault("1");// 默认
			tbItem.setSpec("{}");// 规格

			setItem(goods, tbItem);

			itemMapper.insert(tbItem);
		}

	}
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods) {
		// 插入商品基本信息
		goods.getTbGoods().setAuditStatus("0");// 商品审核状态 0未审核
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());

		// 插入商品扩展信息
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());// 设置主键
		GoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
		
		//删除sku值
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		itemMapper.deleteByExample(example);
		
		setSpec(goods);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {

		Goods goods = new Goods();
		// 商品spu
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setTbGoods(tbGoods);
		// 商品spu扩展
		TbGoodsDesc tbGoodsDesc = GoodsDescMapper.selectByPrimaryKey(tbGoods.getId());
		goods.setTbGoodsDesc(tbGoodsDesc);
		// 商品sku
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(tbGoods.getId());
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			//删除spu
			goodsMapper.deleteByPrimaryKey(id);
			//删除spu扩展
			GoodsDescMapper.deleteByPrimaryKey(id);
			//删除sku
			TbItemExample example = new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			itemMapper.deleteByExample(example);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				criteria.andSellerIdLike(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateAuditStatus(Long [] ids, String status) {
		for (int i = 0; i < ids.length; i++) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(ids[i]);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
		
	}

	@Override
	public List<TbItem> finditemByGoodsIdAndStatus(Long[] ids, String status) {
		
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);
		criteria.andGoodsIdIn(Arrays.asList(ids));
		List<TbItem> itemList = itemMapper.selectByExample(example );
		return itemList;
	}

}
