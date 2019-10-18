package com.bbu.springstudy.community.mapper;

import com.bbu.springstudy.community.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment record);
}