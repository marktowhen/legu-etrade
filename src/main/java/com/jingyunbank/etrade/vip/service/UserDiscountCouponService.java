package com.jingyunbank.etrade.vip.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jingyunbank.core.KeyGen;
import com.jingyunbank.core.Range;
import com.jingyunbank.core.Result;
import com.jingyunbank.etrade.api.exception.DataRefreshingException;
import com.jingyunbank.etrade.api.exception.DataSavingException;
import com.jingyunbank.etrade.api.vip.bo.DiscountCoupon;
import com.jingyunbank.etrade.api.vip.bo.UserDiscountCoupon;
import com.jingyunbank.etrade.api.vip.service.IDiscountCouponService;
import com.jingyunbank.etrade.api.vip.service.IUserDiscountCouponService;
import com.jingyunbank.etrade.base.util.EtradeUtil;
import com.jingyunbank.etrade.vip.dao.UserDiscountCouponDao;
import com.jingyunbank.etrade.vip.entity.DiscountCouponEntity;
import com.jingyunbank.etrade.vip.entity.UserDiscountCouponEntity;

@Service("userDiscountCouponService")
public class UserDiscountCouponService implements IUserDiscountCouponService {
	
	@Autowired
	private UserDiscountCouponDao userDiscountCouponDao ;
	@Autowired
	private IDiscountCouponService discountCouponService;

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public boolean active(String code, String uid) throws DataSavingException, DataRefreshingException {
		DiscountCoupon discountCoupon  = discountCouponService.getSingleByCode(code);
		if(discountCouponService==null){
			return false;
		}
		//插入关联表
		UserDiscountCoupon userDiscountCoupon = new UserDiscountCoupon();
		userDiscountCoupon.setID(KeyGen.uuid());
		userDiscountCoupon.setUID(uid);
		userDiscountCoupon.setCouponID(discountCoupon.getID());
		save(userDiscountCoupon);
		//更改是体表状态
		discountCouponService.active(code);
		return true;
	}
	
	@Override
	public int getUnusedCouponAmount(String uid) {
		return userDiscountCouponDao.getUnusedCouponAmount(uid);
	}
	
	
	@Override
	public List<UserDiscountCoupon> getUnusedCoupon(String uid, Range range) {
		long offset = 0L;
		long size = 0L;
		if(range!=null){
			offset = range.getFrom();
			size = range.getTo()-range.getFrom();
		}
		return userDiscountCouponDao.getUnusedCoupon(uid, offset, size )
			.stream().map(rEntity->{return getBoFromEntity(rEntity);})
			.collect(Collectors.toList());
	}
	
	@Override
	public Result<String> canConsume(String couponId, String uid, BigDecimal orderPrice) {
		UserDiscountCouponEntity entity =  userDiscountCouponDao.getUserDiscountCoupon(couponId,  uid);
		if(entity==null){
			return Result.fail("未找到");
		}
		DiscountCouponEntity discountCoupon = entity.getDiscountCouponEntity();
		if(discountCoupon==null){
			return Result.fail("数据错误");
		}
		if(entity.isConsumed()){
			return  Result.fail("该券已消费");
		}
		if(discountCoupon.isDel()){
			return  Result.fail("该券已被删除");
		}
		Date nowDate = EtradeUtil.getNowDate();
		if(discountCoupon.getStart().after(nowDate)){
			return Result.fail("未到使用时间");
		}
		if(discountCoupon.getEnd().before(nowDate)){
			return Result.fail("已失效");
		}
		if(orderPrice==null || orderPrice.compareTo(discountCoupon.getThreshhold())==-1){
			return Result.fail("未到使用门槛:"+discountCoupon.getThreshhold().doubleValue());
		}
		
		return Result.ok();
	}

	@Override
	public boolean consume(String couponId, String oid) throws DataRefreshingException {
		UserDiscountCouponEntity entity = new UserDiscountCouponEntity();
		entity.setCouponID(couponId);
		entity.setOID(oid);
		try {
			return userDiscountCouponDao.updateConsumeStatus(entity);
		} catch (Exception e) {
			throw new DataRefreshingException(e);
		}
	}
	
	private boolean save(UserDiscountCoupon bo) throws DataSavingException{
		try {
			return userDiscountCouponDao.insert(getEntityFromBo(bo));
		} catch (Exception e) {
			throw new DataSavingException(e);
		}
	}
	
	
	private UserDiscountCoupon getBoFromEntity(UserDiscountCouponEntity entity){
		if(entity!=null){
			UserDiscountCoupon bo = new UserDiscountCoupon();
			BeanUtils.copyProperties(entity, bo);
			if(entity.getDiscountCouponEntity()!=null){
				DiscountCoupon dBo = new DiscountCoupon();
				BeanUtils.copyProperties(entity.getDiscountCouponEntity(), dBo);
				bo.setDiscountCoupon(dBo);
			}
			return bo;
		}
		return null;
	}
	
	private UserDiscountCouponEntity getEntityFromBo(UserDiscountCoupon bo){
		if(bo!=null){
			UserDiscountCouponEntity entity = new UserDiscountCouponEntity();
			BeanUtils.copyProperties(bo, entity);
			if(bo.getDiscountCoupon()!=null){
				DiscountCouponEntity dEntity = new  DiscountCouponEntity();
				BeanUtils.copyProperties(bo.getDiscountCoupon(), dEntity);
				entity.setDiscountCouponEntity(dEntity);
			}
			return entity;
		}
		return null;
	}

	@Override
	public int getConsumedCouponAmount(String uid) {
		return userDiscountCouponDao.getConsumedCouponAmount(uid);
	}

	@Override
	public List<UserDiscountCoupon> getConsumedCoupon(String uid, Range range) {
		long offset = 0L;
		long size = 0L;
		if(range!=null){
			offset = range.getFrom();
			size = range.getTo()-range.getFrom();
		}
		return userDiscountCouponDao.getConsumedCoupon(uid, offset, size )
			.stream().map(rEntity->{return getBoFromEntity(rEntity);})
			.collect(Collectors.toList());
	}

	@Override
	public int getOverdueCouponAmount(String uid) {
		return userDiscountCouponDao.getOverdueCouponAmount(uid);
	}

	@Override
	public List<UserDiscountCoupon> getOverdueCoupon(String uid, Range range) {
		long offset = 0L;
		long size = 0L;
		if(range!=null){
			offset = range.getFrom();
			size = range.getTo()-range.getFrom();
		}
		return userDiscountCouponDao.getOverdueCoupon(uid, offset, size )
			.stream().map(rEntity->{return getBoFromEntity(rEntity);})
			.collect(Collectors.toList());
	}

	@Override
	public int getUseableCouponAmount(String uid) {
		return userDiscountCouponDao.getUseableCouponAmount(uid);
	}

	@Override
	public List<UserDiscountCoupon> getUseableCoupon(String uid, Range range) {
		long offset = 0L;
		long size = 0L;
		if(range!=null){
			offset = range.getFrom();
			size = range.getTo()-range.getFrom();
		}
		return userDiscountCouponDao.getUseableCoupon(uid, offset, size )
			.stream().map(rEntity->{return getBoFromEntity(rEntity);})
			.collect(Collectors.toList());
	}

	

	

	

}
