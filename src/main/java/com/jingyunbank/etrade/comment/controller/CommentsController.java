package com.jingyunbank.etrade.comment.controller;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jingyunbank.core.KeyGen;
import com.jingyunbank.core.Result;
import com.jingyunbank.core.web.AuthBeforeOperation;
import com.jingyunbank.core.web.ServletBox;
import com.jingyunbank.etrade.api.comment.bo.Comments;
import com.jingyunbank.etrade.api.comment.bo.CommentsImg;
import com.jingyunbank.etrade.api.comment.service.ICommentImgService;
import com.jingyunbank.etrade.api.comment.service.ICommentService;
import com.jingyunbank.etrade.comment.bean.CommentsImgVO;
import com.jingyunbank.etrade.comment.bean.CommentsVO;


@Controller
public class CommentsController {
	@Autowired
	private ICommentService commentService;
	@Autowired
	private ICommentImgService commentImgService;
	/**
	 * 保存商品的评论信息和对应的多张图片
	 * @param commentVO
	 * @param commentsImgVO
	 * @param request
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@AuthBeforeOperation
	@RequestMapping(value="/api/comments/list",method=RequestMethod.PUT)
	@ResponseBody
	public Result saveComments(CommentsVO commentVO,CommentsImgVO commentsImgVO,HttpServletRequest request,HttpSession session) throws Exception{
		commentVO.setID(KeyGen.uuid());
		String id = ServletBox.getLoginUID(request);
		commentVO.setUID(id);
		commentVO.setAddtime(new Date());
		commentVO.setCommentStatus(2);
		Comments comments=new Comments();
		BeanUtils.copyProperties(commentVO, comments);

		if(commentService.save(comments)){
			
			/*	String filePath=request.getSession().getServletContext().getRealPath("/") + "upload";
			File uploadDest=new File(filePath);
			String[] fileNames=uploadDest.list();
			for(int i=0;i<fileNames.length;i++){
				commentsImgVO.setId(KeyGen.uuid());
				commentsImgVO.setPicture("a.jpg");
				commentsImgVO.setImgid(commentVO.getImgid());
				CommentsImg commentsImg=new CommentsImg();
				BeanUtils.copyProperties(commentsImgVO, commentsImg);
				commentImgService.save(commentsImg);
			}*/
			//对保存多张图片的过程！模拟写的！有多张图片的保存
			for(int i=0;i<3;i++){
				commentsImgVO.setID(KeyGen.uuid());
				commentsImgVO.setPicture("a.jpg");
				commentsImgVO.setImgID(commentVO.getImgID());
				CommentsImg commentsImg=new CommentsImg();
				BeanUtils.copyProperties(commentsImgVO, commentsImg);
				commentImgService.save(commentsImg);
				}
			return Result.ok("保存成功");
			}
		return Result.fail("保存失败！");
	}
	/**
	 * 测试通过产品的id查出所有的评论信息
	 * @param gid
	 * @param request
	 * @param session
	 * @return
	 */
	@AuthBeforeOperation
	@RequestMapping(value="/api/comments/getbyid/{gid}",method=RequestMethod.GET)
	@ResponseBody
	public Result getComments(@PathVariable String gid,HttpServletRequest request,HttpSession session) throws Exception{
		return Result.ok(commentService.getCommentsByGid(gid)
				.stream().map(bo-> {
					CommentsVO vo= new CommentsVO();
					BeanUtils.copyProperties(bo, vo);
					return vo;
				}).collect(Collectors.toList()));
	}
	/**
	 * 测试通过id删除自己添加的商品评价，别人的评论没有权限删除
	 * @param id
	 * @param request
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@AuthBeforeOperation
	@RequestMapping(value="/api/comments/delete/{id}",method=RequestMethod.DELETE)
	@ResponseBody
	public Result remove(@PathVariable String id,HttpServletRequest request,HttpSession session) throws Exception{
		String uid = ServletBox.getLoginUID(request);
		Optional<Comments> comments=commentService.getById(id);
		CommentsVO commentsVO=new CommentsVO();
		BeanUtils.copyProperties(comments.get(), commentsVO);
		if(commentsVO.getUID().equals(uid)){
			commentImgService.remove(commentsVO.getImgID());
			commentService.remove(id);
			return Result.ok("删除成功");
		}
		return Result.fail("没有删除的权限");
	}
	/**
	 * 修改评论的状态
	 * @param commentsVO
	 * @param request
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@AuthBeforeOperation
	@RequestMapping(value="/api/comments/update/status",method=RequestMethod.POST)
	@ResponseBody
	public Result updateStatus(CommentsVO commentsVO,HttpServletRequest request,HttpSession session) throws Exception{
		
		Optional<Comments> optionComments=commentService.getById(commentsVO.getID());
		Comments comments=	optionComments.get();
	
		if(!StringUtils.isEmpty(comments.getGoodsComment())){
			//如果已经评价就把设置为2
			
			comments.setCommentStatus(2);
			commentService.refreshStatus(comments);
			
			BeanUtils.copyProperties(comments, commentsVO);
			return Result.ok(commentsVO);
		}
		return Result.fail("请重试");
		
	}
}
