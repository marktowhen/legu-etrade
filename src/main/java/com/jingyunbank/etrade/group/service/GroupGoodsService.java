package com.jingyunbank.etrade.group.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jingyunbank.etrade.api.exception.DataSavingException;
import com.jingyunbank.etrade.api.group.bo.GroupGoods;
import com.jingyunbank.etrade.api.group.bo.GroupGoodsPriceSetting;
import com.jingyunbank.etrade.api.group.service.IGroupGoodsService;

@Service("groupGoodsService")
public class GroupGoodsService implements IGroupGoodsService {

	@Override
	public void save(GroupGoods goods) throws DataSavingException {
		List<GroupGoodsPriceSetting> priceSettings = goods.getPriceSettings();
		//save goods
		//save price setting
		
	}

	@Override
	public Optional<GroupGoods> single(String ggid) {
		return Optional.ofNullable(null);
	}

}
