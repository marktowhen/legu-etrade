package com.jingyunbank.etrade.comment.dao;

import java.util.List;

import com.jingyunbank.etrade.comment.entity.CommentsEntity;

public interface CommentsDao {
	
	public int insert(CommentsEntity commentsEntity) throws Exception;
	
	public List<CommentsEntity> selectCommentByGid(String gid); 
	
	public CommentsEntity selectById(String id);
	
	public void delete(String id) throws Exception;
	
	public boolean updateStatus(CommentsEntity commentsEntity) throws Exception;
	
/*	public List<CommentsEntity> selectList();*/
	
	public boolean refreshReadStatus(CommentsEntity commentsEntity) throws Exception;
	
	public CommentsEntity selectCommentByOid(String oid);
	
}
