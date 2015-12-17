package com.jingyunbank.etrade.merchant.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jingyunbank.core.KeyGen;
import com.jingyunbank.core.Page;
import com.jingyunbank.core.Range;
import com.jingyunbank.core.Result;
import com.jingyunbank.etrade.api.merchant.bo.DeliveryType;
import com.jingyunbank.etrade.api.merchant.bo.InvoiceType;
import com.jingyunbank.etrade.api.merchant.bo.Merchant;
import com.jingyunbank.etrade.api.merchant.service.IMerchantService;
import com.jingyunbank.etrade.api.merchant.service.context.IMerchantContextService;
import com.jingyunbank.etrade.merchant.bean.DeliveryTypeVO;
import com.jingyunbank.etrade.merchant.bean.InvoiceTypeVO;
import com.jingyunbank.etrade.merchant.bean.MerchantSearchVO;
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
	public Result<List<MerchantVO>> recommend(HttpServletRequest request, HttpSession session) throws Exception{
		//转成VO
		List<Merchant> list = merchantService.listMerchants();
		List<MerchantVO> rlist = new ArrayList<MerchantVO>();
		MerchantVO vo = null;
		for(Merchant bo : list){
			vo = MerchantVO.getInstance();
			BeanUtils.copyProperties(bo,vo);
			rlist.add(vo);
		}
		return Result.ok(rlist);
	}
	/**
	 * 商家保存
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/savemerchant", method = RequestMethod.POST)
	public Result<MerchantVO> saveMerchant(HttpServletRequest request, HttpSession session,@RequestBody @Valid MerchantVO merchantVO,BindingResult valid) throws Exception {
		// 异常信息
		if (valid.hasErrors()) {
			List<ObjectError> errors = valid.getAllErrors();
			return Result.fail(errors.stream().map(oe -> Arrays.asList(oe.getDefaultMessage()).toString())
					.collect(Collectors.joining(" ; ")));
		}
		Merchant merchant=Merchant.getInstance();
		merchantVO.setAdminSortNum(0);
		merchantVO.setID(KeyGen.uuid());
		merchant.setRegisterDate(new Date());
		BeanUtils.copyProperties(merchantVO, merchant);
		if(merchantContextService.saveMerchant(merchant)){
			return Result.ok(merchantVO);
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
	public Result<List<InvoiceTypeVO>> getInvoiceType(HttpServletRequest request, HttpSession session) throws Exception{
		//转成VO
		List<InvoiceType> list = merchantService.listInvoiceType();
		List<InvoiceTypeVO> rlist = new ArrayList<InvoiceTypeVO>();
		InvoiceTypeVO vo = null;
		for(InvoiceType bo : list){
			vo = new InvoiceTypeVO();
			BeanUtils.copyProperties(bo,vo);
			rlist.add(vo);
		}
		return Result.ok(rlist);
	}
	/**
	 * 商家修改
	 * 
	 * @return
	 * @throws Exception
	 */
	//@AuthBeforeOperation
	@RequestMapping(value = "/updatemerchant", method = RequestMethod.POST)
	public Result<MerchantVO> updateMerchant(HttpServletRequest request, HttpSession session,@RequestBody @Valid MerchantVO merchantVO,BindingResult valid) throws Exception{
		// 异常信息
		if (valid.hasErrors()) {
			List<ObjectError> errors = valid.getAllErrors();
			return Result.fail(errors.stream().map(oe -> Arrays.asList(oe.getDefaultMessage()).toString())
					.collect(Collectors.joining(" ; ")));
		}
		Merchant merchant=Merchant.getInstance();
		BeanUtils.copyProperties(merchantVO, merchant);
		//修改商家和修改商家类型
		if(this.merchantContextService.updateMerchant(merchant)){
			return Result.ok(merchantVO);
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
	public Result<List<DeliveryTypeVO>> getDeliveryType(HttpServletRequest request, HttpSession session) throws Exception{
		//转成VO
		List<DeliveryType> list = merchantService.listDeliveryType();
		List<DeliveryTypeVO> rlist = new ArrayList<DeliveryTypeVO>();
		DeliveryTypeVO vo = null;
		for(DeliveryType bo : list){
			vo = new DeliveryTypeVO();
			BeanUtils.copyProperties(bo,vo);
			rlist.add(vo);
		}
		return Result.ok(rlist);
	}
	
	/**
	 * 通过mid查询商家信息	
	 * @param session
	 * @param request
	 * @param uid
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value="/info/{mid}",method=RequestMethod.GET)
	public Result<?> getMerchantInfo(HttpSession session,HttpServletRequest request,@PathVariable String mid) throws Exception{
		Optional<Merchant> merchant= merchantContextService.getMerchantInfoByMid(mid);
		if(merchant.isPresent()){
			Merchant bo = merchant.get();
			MerchantVO vo = new MerchantVO();
			BeanUtils.copyProperties(bo,vo);
			return Result.ok(vo);
		}else{
			return Result.fail("查询没有数据！");
		}
	}
	
	/**
	 * 根据条件查询商家列表
	 * @param request
	 * @param merchantSearchVO
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<List<MerchantVO>> queryMerchantsByCondition(HttpServletRequest request, MerchantSearchVO merchantSearchVO, Page page)
			throws Exception {
		Range range = new Range();
		range.setFrom(page.getOffset());
		range.setTo(page.getSize());
		Merchant merchant = new Merchant();
		BeanUtils.copyProperties(merchantSearchVO, merchant);
		
		List<MerchantVO> merchantlist = merchantService.listMerchantsByCondition(merchant, range).stream().map(bo -> {
			MerchantVO vo = new MerchantVO();
			BeanUtils.copyProperties(bo, vo);
			return vo;
		}).collect(Collectors.toList());
		
		return Result.ok(merchantlist);
	}
	
}
