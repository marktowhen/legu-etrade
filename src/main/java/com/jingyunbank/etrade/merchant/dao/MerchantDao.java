package com.jingyunbank.etrade.merchant.dao;

import java.util.List;
import java.util.Map;

import com.jingyunbank.etrade.api.exception.DataRefreshingException;
import com.jingyunbank.etrade.merchant.entity.DeliveryTypeEntity;
import com.jingyunbank.etrade.merchant.entity.InvoiceTypeEntity;
import com.jingyunbank.etrade.merchant.entity.MerchantDeliveryEntity;
import com.jingyunbank.etrade.merchant.entity.MerchantEntity;
import com.jingyunbank.etrade.merchant.entity.MerchantInvoiceEntity;

/**
 * @author liug
 *
 */
public interface MerchantDao{
	/**
	 * 商家查询
	 * @param params
	 * @return
	 */
	public List<MerchantEntity> selectMerchants(Map<String, Integer> params) ;
	/**
	 * 商家保存
	 * @param merchantEntity
	 * @return
	 * @throws Exception
	 */
	public boolean insertMerchant(MerchantEntity merchantEntity) throws Exception;
	/**
	 * 查询所有的发票类型列表
	 * @return
	 */
	public List<InvoiceTypeEntity> selectInvoiceType() ;
	/**
	 * 商家的发票类型保存
	 * @param merchantEntity
	 * @return
	 * @throws Exception
	 */
	public boolean insertMerchantInvoiceType(MerchantInvoiceEntity merchantInvoiceEntity) throws Exception;
	/**
	 * 商家修改
	 * @param merchantEntity
	 * @return
	 * @throws Exception
	 */
	public boolean updateMerchant(MerchantEntity merchantEntity) throws DataRefreshingException;
	/**
	 * 删除商家和发票类型关联记录
	 * @param id
	 * @throws Exception
	 */
	public boolean deleteMerchantInvoiceType(String mid) throws Exception;
	/**
	 * 查询所有的发票类型列表
	 * @return
	 */
	public List<DeliveryTypeEntity> selectDeliveryType();
	/**
	 * 商家的快递类型保存
	 * @param merchantEntity
	 * @return
	 * @throws Exception
	 */
	public boolean insertMerchantDeliveryType(MerchantDeliveryEntity merchantDeliveryEntity) throws Exception;
	/**
	 * 删除商家和快递类型关联记录
	 * @param id
	 * @throws Exception
	 */
	public boolean deleteMerchantDeliveryType(String mid) throws Exception;
	
}
