package com.jingyunbank.etrade.comment.dao;

import java.util.List;

import com.jingyunbank.etrade.comment.entity.CommentsImgEntity;

public interface CommentsImgDao {
	
	public int insert(CommentsImgEntity commentsImgEntity) throws Exception;
	
	public List<CommentsImgEntity> selectById(String id);
	
	public void delete(String id) throws Exception;
}
