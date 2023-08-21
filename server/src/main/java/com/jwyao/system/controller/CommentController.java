package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Comment;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 评论管理
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Comment> list = commentService.getCommentList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/listThingComments", method = RequestMethod.GET)
    public APIResponse listThingComments(Long thingId, String order) {
        List<Comment> list = commentService.getThingCommentList(thingId, order);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/listUserComments", method = RequestMethod.GET)
    public APIResponse listUserComments(Long userId) {
        List<Comment> list = commentService.getUserCommentList(userId);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Comment comment) {
        commentService.createComment(comment);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            commentService.deleteComment(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Comment comment) {
        commentService.updateComment(comment);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public APIResponse like(Long id) {
        Comment comment = commentService.getCommentDetail(id);
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentService.updateComment(comment);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
