package com.jingyunbank.etrade.merchant.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jingyunbank.core.KeyGen;
import com.jingyunbank.core.Result;
import com.jingyunbank.etrade.api.merchant.bo.DeliveryType;
import com.jingyunbank.etrade.api.merchant.bo.InvoiceType;
import com.jingyunbank.etrade.api.merchant.bo.Merchant;
import com.jingyunbank.etrade.api.merchant.service.IMerchantService;
import com.jingyunbank.etrade.api.merchant.service.context.IMerchantContextService;
import com.jingyunbank.etrade.merchant.bean.DeliveryTypeVO;
import com.jingyunbank.etrade.merchant.bean.InvoiceTypeVO;
import com.jingyunbank.etrade.merchant.bean.MerchantVO;
/**
 * 商家管理控制器
 * @author liug
 *
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
	@Resource
	private IMerchantService merchantService;
	@Autowired
	private IMerchantContextService merchantContextService;
	/**
	 * 推荐商家检索
	 * @param request
	 * @param session
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/recommend/list")
	public Result recommend(HttpServletRequest request, HttpSession session) throws IllegalAccessException, InvocationTargetException{
		//转成VO
		List<Merchant> list = merchantService.listMerchants();
		List<MerchantVO> rlist = new ArrayList<MerchantVO>();
		MerchantVO vo = null;
		for(Merchant bo : list){
			vo = MerchantVO.getInstance();
			BeanUtils.copyProperties(bo,vo);
			rlist.add(vo);
		}
		Result r = Result.ok(list);
		return r;
	}
	/**
	 * 商家保存
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/savemerchant", method = RequestMethod.POST)
	public Result saveMerchant(HttpServletRequest request, HttpSession session,MerchantVO merchantVO) throws Exception {
		//String uid = ServletBox.getLoginUID(request);
		Merchant merchant=Merchant.getInstance();
		merchantVO.setID(KeyGen.uuid());
		merchant.setRegisterDate(new Date());
		BeanUtils.copyProperties(merchantVO, merchant);
		if(merchantContextService.saveMerchant(merchant)){
			return Result.ok("保存成功");
		}
		return Result.ok(merchantVO);
	}
	/**
	 * 获取发票类型
	 * @param request
	 * @param session
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@RequestMapping("/invoicetype/list")
	public Result getInvoiceType(HttpServletRequest request, HttpSession session) throws IllegalAccessException, InvocationTargetException{
		//转成VO
		List<InvoiceType> list = merchantService.listInvoiceType();
		List<InvoiceTypeVO> rlist = new ArrayList<InvoiceTypeVO>();
		InvoiceTypeVO vo = null;
		for(InvoiceType bo : list){
			vo = new InvoiceTypeVO();
			BeanUtils.copyProperties(bo,vo);
			rlist.add(vo);
		}
		Result r = Result.ok(list);
		return r;
	}
	/**
	 * 商家修改
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updatemerchant", method = RequestMethod.POST)
	public Result updateMerchant(HttpServletRequest request, HttpSession session,MerchantVO merchantVO) throws Exception{
		Merchant merchant=Merchant.getInstance();
		BeanUtils.copyProperties(merchantVO, merchant);
		//修改商家和修改商家类型
		if(this.merchantContextService.updateMerchant(merchant)){
			return Result.ok("修改成功");
		}
		return Result.ok(merchantVO);
	}
	 
	/**
	 * 获取快递类型
	 * @param request
	 * @param session
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@RequestMapping("/deliverytype/list")
	public Result getDeliveryType(HttpServletRequest request, HttpSession session) throws IllegalAccessException, InvocationTargetException{
		//转成VO
		List<DeliveryType> list = merchantService.listDeliveryType();
		List<DeliveryTypeVO> rlist = new ArrayList<DeliveryTypeVO>();
		DeliveryTypeVO vo = null;
		for(DeliveryType bo : list){
			vo = new DeliveryTypeVO();
			BeanUtils.copyProperties(bo,vo);
			rlist.add(vo);
		}
		Result r = Result.ok(list);
		return r;
	}
	
}
