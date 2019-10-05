package com.bbu.springstudy.community.service;

import com.bbu.springstudy.community.dto.PaginationDTO;
import com.bbu.springstudy.community.dto.QuestionDTO;
import com.bbu.springstudy.community.exception.CustomizeErrorCode;
import com.bbu.springstudy.community.exception.CustomizeException;
import com.bbu.springstudy.community.mapper.QuestionExtMapper;
import com.bbu.springstudy.community.mapper.QuestionMapper;
import com.bbu.springstudy.community.mapper.UserMapper;
import com.bbu.springstudy.community.model.Question;
import com.bbu.springstudy.community.model.QuestionExample;
import com.bbu.springstudy.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 组装作用
 */
@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer userId, Integer page, Integer size) {


        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample questionExample = new QuestionExample();

        questionExample.createCriteria().andCreatorEqualTo(userId);

        Integer totalCount = Math.toIntExact(questionMapper.countByExample(questionExample));

        paginationDTO.setPagination(totalCount, page, size);

        if(page < 1){
            page = 1;
        }
        if(page > paginationDTO.getTotalPage()){
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page - 1);

        QuestionExample questionExample1 = new QuestionExample();
        questionExample1.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample1,new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//根据变量名称通过反射 注入复制属性
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);


        return paginationDTO;
    }
    public PaginationDTO list(Integer page, Integer size) {


        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
        paginationDTO.setPagination(totalCount, page, size);

        if(page < 1){
            page = 1;
        }
        if(page > paginationDTO.getTotalPage()){
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page - 1);

        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//根据变量名称通过反射 注入复制属性
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);


        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(Long.valueOf(id));
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);

        User user = userMapper.selectByPrimaryKey(questionDTO.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtModified());
            questionMapper.insert(question);
        }else{
            question.setGmtModified(System.currentTimeMillis());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int questionUpdate = questionMapper.updateByExampleSelective(question, questionExample);

            if (questionUpdate != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Integer id) {
        //高并发下 会出现读 脏数据
//        Question question = questionMapper.selectByPrimaryKey((long) id);
//        Question updateQuestion = new Question();
//        updateQuestion.setId((long)id);
//        updateQuestion.setViewCount(question.getViewCount() + 1);
//        questionMapper.updateByPrimaryKeySelective(updateQuestion);

        Question question = new Question();
        question.setId((long)id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
