package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.Comment;
import com.jwyao.system.mapper.CommentMapper;
import com.jwyao.system.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> getCommentList() {
        return commentMapper.getList();
    }

    @Override
    public void createComment(Comment comment) {
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(1);
        }
        commentMapper.insert(comment);
    }

    @Override
    public void deleteComment(String id) {
        commentMapper.deleteById(id);
    }

    @Override
    public void updateComment(Comment comment) {
        commentMapper.updateById(comment);
    }

    @Override
    public Comment getCommentDetail(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public List<Comment> getThingCommentList(Long thingId, String order) {
        return commentMapper.selectThingCommentList(thingId, order);
    }

    @Override
    public List<Comment> getUserCommentList(Long userId) {
        return commentMapper.selectUserCommentList(userId);
    }

}
