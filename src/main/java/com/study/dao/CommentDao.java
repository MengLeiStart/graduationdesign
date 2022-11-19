package com.study.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDao extends BaseMapper<Comment> {
}
