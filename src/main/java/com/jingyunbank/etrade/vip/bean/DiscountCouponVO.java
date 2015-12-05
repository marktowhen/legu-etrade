package com.jingyunbank.etrade.vip.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 折扣抵用券<br>
 * <strong>规则：</strong><br>
 * 结账时的商品金额可抵扣20%；<br>
 * 不能抵扣运费（即运费部分另计，只对商品部分进行打折）；<br>
 * 一张抵用券只能在一张订单中使用，且不找零；<br>
 * 抵用券不可与优惠券购物金叠加使用。
 */
public class DiscountCouponVO extends BaseCouponVO implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3034128223623799548L;
	private String ID;
	private String code;//充值码
	
	@NotNull(message="折扣不能为空")
	@DecimalMin(value="0.01", message="折扣须在0.01到0.99之间")
	@DecimalMax(value="0.99", message="折扣须在0.01到0.99之间")
	private BigDecimal discount;//折扣
	
	@JsonFormat(pattern="yyyy-MM-dd" ,locale="zh", timezone="GMT+8")
	private Date addtime;
	
	@NotNull(message="开始时间不能为空")
	@JsonFormat(pattern="yyyy-MM-dd" ,locale="zh", timezone="GMT+8")
	private Date start;
	
	@NotNull(message="结束时间不能为空")
	@JsonFormat(pattern="yyyy-MM-dd" ,locale="zh", timezone="GMT+8")
	private Date end;
	
	private boolean used;//是否充值到某用户账户中
	
	@JsonFormat(pattern="yyyy-MM-dd" ,locale="zh", timezone="GMT+8")
	private Date usedtime;//充值时间
	
	@NotNull(message="使用门槛不能为空")
	@DecimalMin(value="0.00")
	private BigDecimal threshhold;//使用门槛
	
	@NotNull(message="金额不能为空")
	@DecimalMin(value="0.01", message="价值不能低于1分")
	private BigDecimal value;//面值 抵用的最高值
	
	
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public BigDecimal getThreshhold() {
		return threshhold;
	}
	public void setThreshhold(BigDecimal threshhold) {
		this.threshhold = threshhold;
	}
	public Date getUsedtime() {
		return usedtime;
	}
	public void setUsedtime(Date usedtime) {
		this.usedtime = usedtime;
	}
	
}
