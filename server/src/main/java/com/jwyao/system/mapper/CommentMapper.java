package com.jwyao.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwyao.system.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getList();

    List<Comment> selectThingCommentList(Long thingId, String order);

    List<Comment> selectUserCommentList(Long userId);

    Integer getThingCommentCount(Long thingId);

}
