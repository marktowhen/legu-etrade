package com.jingyunbank.etrade.vip.coupon.bean;

import java.io.Serializable;

/**
 * 所有优惠方式的父类。
 */
public class BaseCouponVO implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 113271790301425523L;
	
	private boolean needUsed;//查询时是否需要加上used这个字段的条件
	private boolean needDelete;//查询时是否需要加上Delete这个字段的条件
	
	private boolean del;//是否被删除
	private String remark;//备注 
	private String cardNum;//卡号
	private boolean locked;//是否锁定
	
	
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isNeedUsed() {
		return needUsed;
	}
	public void setNeedUsed(boolean needUsed) {
		this.needUsed = needUsed;
	}
	public boolean isNeedDelete() {
		return needDelete;
	}
	public void setNeedDelete(boolean needDelete) {
		this.needDelete = needDelete;
	}
	
	public boolean isDel() {
		return del;
	}
	public void setDel(boolean del) {
		this.del = del;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
