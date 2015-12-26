package com.jingyunbank.etrade.order.presale.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jingyunbank.core.KeyGen;
import com.jingyunbank.core.Result;
import com.jingyunbank.core.util.UniqueSequence;
import com.jingyunbank.core.web.AuthBeforeOperation;
import com.jingyunbank.core.web.ServletBox;
import com.jingyunbank.etrade.api.order.presale.bo.OrderGoods;
import com.jingyunbank.etrade.api.order.presale.bo.OrderStatusDesc;
import com.jingyunbank.etrade.api.order.presale.bo.Orders;
import com.jingyunbank.etrade.api.order.presale.service.context.IOrderContextService;
import com.jingyunbank.etrade.api.vip.coupon.handler.ICouponStrategyResolver;
import com.jingyunbank.etrade.api.vip.coupon.handler.ICouponStrategyService;
import com.jingyunbank.etrade.cart.controller.CartController;
import com.jingyunbank.etrade.order.presale.bean.PurchaseGoodsVO;
import com.jingyunbank.etrade.order.presale.bean.PurchaseOrderVO;
import com.jingyunbank.etrade.order.presale.bean.PurchaseRequestVO;

@RestController
public class OrderController {

	@Autowired
	private IOrderContextService orderContextService;
	@Autowired
	private ICouponStrategyResolver couponStrategyResolver;
	
	/**
	 * 订单确认并提交<br>
	 * uri: put /api/order {"addressID":"XXXXX", "":"", "orders":[{}, {}, {}]}
	 * @param purchase
	 * @param valid
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@AuthBeforeOperation
	@RequestMapping(
			value="/api/order",
			method=RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Result<PurchaseRequestVO> submit(@Valid @RequestBody PurchaseRequestVO purchase,
			BindingResult valid, HttpSession session) throws Exception{
		if(valid.hasErrors()){
			return Result.fail("您提交的订单数据不完整，请核实后重新提交！");
		}
		String UID = ServletBox.getLoginUID(session);
		purchase.setUID(UID);

		String couponID = purchase.getCouponID();
		String couponType = purchase.getCouponType();
		
		List<Orders> orders = populateOrderData(purchase, session);
		
		//订单价格简单校验
		//订单价应担匹配商品总价及邮费计算规则
		boolean goodData = verifyOrderData(orders);
		if(!goodData){
			return Result.fail("订单数据校验失败，请检查订单信息后重新提交。");
		}
		
		boolean legalCoupon = calculateOrdersGoodsPayout(couponID, couponType, UID, orders);
		if(!legalCoupon){
			return Result.fail("无效的订单优惠卡券，请检查订单信息后重新提交。");
		}
		
		orderContextService.save(orders);
		session.removeAttribute(CartController.GOODS_IN_CART_TO_CLEARING);
		return Result.ok(purchase);
	}

	@AuthBeforeOperation
	@RequestMapping(
			value="/api/orders/cancellation",
			method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Result<String> cancel(@Valid @RequestBody OIDWithNoteVO cancellation,
			BindingResult valid, HttpSession session) throws Exception{
		if(valid.hasErrors()){
			return Result.fail("您提交的订单信息有误！");
		}
		if(!orderContextService.cancel(cancellation.getOid(), cancellation.getNote())){
			return Result.fail("您提交的订单信息有误，请检查后重新尝试！");
		}
		return Result.ok();
	}
	
	@AuthBeforeOperation
	@RequestMapping(value="/api/orders/{id}", method=RequestMethod.DELETE)
	public Result<String> remove(@PathVariable String id) throws Exception{
		
		orderContextService.remove(id);
		
		return Result.ok(id);
	}
	
	
	
	
	
	public final static BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal(99);//包邮下限
	private boolean verifyOrderData(List<Orders> orders) {
		for (Orders order : orders) {
			BigDecimal originorderprice = order.getPrice();//data from user.
			BigDecimal originorderpostage = order.getPostage();//data from user.
			BigDecimal calculatedorderprice = BigDecimal.ZERO;//data calculated based on goods info.
			BigDecimal calculatedorderpostage = BigDecimal.ZERO;//as above.
			
			List<OrderGoods> goods = order.getGoods();
			for (OrderGoods orderGoods : goods) {
				BigDecimal pprice = orderGoods.getPprice();//data from user.
				BigDecimal price = orderGoods.getPrice();//data from user.
				BigDecimal postage = orderGoods.getPostage();//data from user.
				int count = orderGoods.getCount();//data from user.
				BigDecimal actualprice = (Objects.nonNull(pprice) && pprice.compareTo(BigDecimal.ZERO) > 0)?
									pprice : price;
				calculatedorderprice = calculatedorderprice.add(actualprice.multiply(BigDecimal.valueOf(count)).setScale(2, RoundingMode.HALF_UP));
				calculatedorderpostage = calculatedorderpostage.add(postage);
			}
			//包邮
			if(calculatedorderprice.compareTo(FREE_SHIPPING_THRESHOLD) >= 0){
				calculatedorderpostage = BigDecimal.ZERO;
			}
			calculatedorderprice = calculatedorderprice.add(calculatedorderpostage);
			if(calculatedorderprice.compareTo(originorderprice) != 0
					|| calculatedorderpostage.compareTo(originorderpostage) != 0){
				return false;
			}
		}
		return true;
	}

	private List<Orders> populateOrderData(PurchaseRequestVO purchase,
			HttpSession session) throws Exception {
		
		List<PurchaseOrderVO> ordervos = purchase.getOrders();
		List<Orders> orders = new ArrayList<Orders>();
		
		for (PurchaseOrderVO ordervo : ordervos) {
			ordervo.setID(KeyGen.uuid());
			ordervo.setOrderno(UniqueSequence.next18());
			ordervo.setAddtime(new Date());
			
			Orders order = new Orders();
			BeanUtils.copyProperties(ordervo, order);
			BeanUtils.copyProperties(purchase, order);
			
			order.setStatusCode(OrderStatusDesc.NEW.getCode());
			order.setStatusName(OrderStatusDesc.NEW.getName());
			
			List<PurchaseGoodsVO> goodses = ordervo.getGoods();
			List<OrderGoods> orderGoodses = new ArrayList<OrderGoods>();
			for (PurchaseGoodsVO goods : goodses) {
				OrderGoods orderGoods = new OrderGoods();
				BeanUtils.copyProperties(goods, orderGoods);
				orderGoods.setID(KeyGen.uuid());
				orderGoods.setOID(order.getID());
				orderGoods.setOrderno(order.getOrderno());
				orderGoods.setStatusCode(OrderStatusDesc.NEW_CODE);
				orderGoods.setAddtime(new Date());
				orderGoods.setUID(order.getUID());
				orderGoodses.add(orderGoods);
			}
			
			order.setGoods(orderGoodses);
			orders.add(order);
		}
		
		return orders;
	}
	
	//计算订单，及订单中每件商品的实际支付价格(剔除使用优惠卡券后的价格)
	private boolean calculateOrdersGoodsPayout(String couponID, String couponType, String UID,
											List<Orders> orders) throws Exception{
		if(StringUtils.hasText(couponType) && StringUtils.hasText(couponID)){
			BigDecimal origintotalprice = orders.stream()
										.map(x->x.getPrice())
										.reduce(new BigDecimal(0), (x,y)->x.add(y));
			ICouponStrategyService couponStrategyService = couponStrategyResolver.resolve(couponType);
			Result<BigDecimal> finalpricer = couponStrategyService.calculate(UID, couponID, origintotalprice);
			if(finalpricer.isBad()) return false;//illegal coupon
			
			BigDecimal finalprice = finalpricer.getBody();
			orders.forEach(order -> {
				BigDecimal orderprice = order.getPrice();
				//优惠百分比
				BigDecimal orderpricepercent = orderprice.divide(origintotalprice, 6, RoundingMode.HALF_UP);
				BigDecimal neworderprice = finalprice.multiply(orderpricepercent).setScale(2, RoundingMode.HALF_UP);
				order.setPayout(neworderprice);
				List<OrderGoods> goodses = order.getGoods();
				goodses.forEach(goods -> {
					BigDecimal origingoodspprice = goods.getPprice();//促销价
					BigDecimal origingoodsprice = goods.getPrice();
					origingoodsprice = //如果促销价不为空，则使用促销价
							(Objects.nonNull(origingoodspprice) && origingoodspprice.compareTo(new BigDecimal(0)) > 0)?
							origingoodspprice : origingoodsprice;
					BigDecimal origingoodspricepercent = origingoodsprice.divide(orderprice, 6, RoundingMode.HALF_UP);
					BigDecimal finalgoodsprice = origingoodspricepercent.multiply(neworderprice).setScale(2, RoundingMode.HALF_UP);
					goods.setPayout(finalgoodsprice);
					goods.setCouponReduce(origingoodsprice.subtract(finalgoodsprice));
				});
			});
		}else{
			orders.forEach(order->{
				order.setPayout(order.getPrice());
				List<OrderGoods> goodses = order.getGoods();
				goodses.forEach(goods -> {
					goods.setPayout(goods.getPrice());
					goods.setCouponReduce(BigDecimal.ZERO);
				});
			});
		}
		return true;
	}
	
	

	private static class OIDWithNoteVO{
		@NotNull
		private String oid;
		@NotNull
		private String note;
		public String getOid() {
			return oid;
		}
		@SuppressWarnings("unused")
		public void setOid(String oid) {
			this.oid = oid;
		}
		public String getNote() {
			return note;
		}
		@SuppressWarnings("unused")
		public void setNote(String note) {
			this.note = note;
		}
	}
	
}
