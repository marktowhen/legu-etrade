package com.jingyunbank.etrade.logistic.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jingyunbank.etrade.api.exception.DataRefreshingException;
import com.jingyunbank.etrade.api.exception.DataRemovingException;
import com.jingyunbank.etrade.api.exception.DataSavingException;
import com.jingyunbank.etrade.api.logistic.bo.PostageDetail;
import com.jingyunbank.etrade.api.logistic.service.IPostageDetailService;
import com.jingyunbank.etrade.logistic.dao.PostageDetailDao;
import com.jingyunbank.etrade.logistic.entity.PostageDetailEntity;

@Service("postageDetailService")
public class PostageDetailService implements IPostageDetailService {
	
	@Autowired
	private PostageDetailDao postageDetailDao;

	@Override
	public boolean save(PostageDetail detail) throws DataSavingException {
		PostageDetailEntity entity = new PostageDetailEntity();
		BeanUtils.copyProperties(detail, entity);
		try {
			return postageDetailDao.insert(entity);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean refresh(PostageDetail detail) throws DataRefreshingException {
		
		PostageDetailEntity entity = new PostageDetailEntity();
		BeanUtils.copyProperties(detail, entity);
		try {
			return postageDetailDao.update(entity);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public PostageDetail single(String ID) {
		PostageDetailEntity entity = postageDetailDao.selectOne(ID);
		if(entity!=null){
			PostageDetail bo = new PostageDetail();
			BeanUtils.copyProperties(entity, bo);
			return bo;
		}
		return null;
	}

	@Override
	public List<PostageDetail> list(String postageID) {
		return postageDetailDao.selectByPostageID(postageID).stream().map(  entity->{
			PostageDetail bo = new PostageDetail();
			BeanUtils.copyProperties(entity, bo);
			return bo;
		}).collect(Collectors.toList());
	}

	@Override
	public boolean remove(String ID) throws DataRemovingException {
		try {
			return postageDetailDao.updateStatus(ID, false);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean removeByPostageID(String postageID)
			throws DataRemovingException {
		try {
			return postageDetailDao.updateStatusBatch(postageID, false);
		} catch (Exception e) {
			return false;
		}
	}

}
