package com.jwyao.system.service;

import com.jwyao.system.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentList();

    void createComment(Comment comment);

    void deleteComment(String id);

    void updateComment(Comment comment);

    Comment getCommentDetail(Long id);

    List<Comment> getThingCommentList(Long thingId, String order);

    List<Comment> getUserCommentList(Long userId);

}
