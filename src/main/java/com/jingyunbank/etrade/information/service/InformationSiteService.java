package com.jingyunbank.etrade.information.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jingyunbank.core.Range;
import com.jingyunbank.etrade.api.exception.DataSavingException;
import com.jingyunbank.etrade.api.information.bo.InformationSite;
import com.jingyunbank.etrade.api.information.service.IInformationSiteService;
import com.jingyunbank.etrade.information.dao.InformationSiteDao;
import com.jingyunbank.etrade.information.entity.InformationSiteEntity;
@Service
public class InformationSiteService implements IInformationSiteService{
	
	@Autowired
	private InformationSiteDao informationSiteDao;
	//添加多个标题信息
	@Override
	public boolean save(InformationSite informationSite) throws DataSavingException {
		boolean flag;
		// TODO Auto-generated method stub
		InformationSiteEntity informationSiteEntity=new InformationSiteEntity();
		BeanUtils.copyProperties(informationSite, informationSiteEntity);
		try {
			if(informationSiteDao.insert(informationSiteEntity)){
				flag=true;
			}else{
				flag=false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DataSavingException(e);
		}
		return flag;
	}

	@Override
	public List<InformationSite> list(String informationID, Range range) {
		
		return informationSiteDao.select(informationID, range.getFrom(),range.getTo()-range.getFrom())
				.stream().map(entity ->{
					InformationSite bo=new InformationSite();
					BeanUtils.copyProperties(entity, bo);
					return bo;
					
				}).collect(Collectors.toList());
	}
	@Override
	public List<InformationSite> list(String informationID) {
		// TODO Auto-generated method stub
		return informationSiteDao.selectSites(informationID)
				.stream().map(entity ->{
					InformationSite bo=new InformationSite();
					BeanUtils.copyProperties(entity, bo);
					return bo;
					
				}).collect(Collectors.toList());
	}

}
