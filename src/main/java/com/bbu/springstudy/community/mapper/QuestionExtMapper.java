package com.bbu.springstudy.community.mapper;

import com.bbu.springstudy.community.dto.QuestionQueryDTO;
import com.bbu.springstudy.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);
    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}