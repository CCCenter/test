package com.bbu.springstudy.community.service;

import com.bbu.springstudy.community.dto.PaginationDTO;
import com.bbu.springstudy.community.dto.QuestionDTO;
import com.bbu.springstudy.community.dto.QuestionQueryDTO;
import com.bbu.springstudy.community.exception.CustomizeErrorCode;
import com.bbu.springstudy.community.exception.CustomizeException;
import com.bbu.springstudy.community.mapper.QuestionExtMapper;
import com.bbu.springstudy.community.mapper.QuestionMapper;
import com.bbu.springstudy.community.mapper.UserMapper;
import com.bbu.springstudy.community.model.Question;
import com.bbu.springstudy.community.model.QuestionExample;
import com.bbu.springstudy.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public PaginationDTO list(Long userId, Integer page, Integer size) {

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

        paginationDTO.setData(questionDTOList);


        return paginationDTO;
    }
    public PaginationDTO list(String search, Integer page, Integer size) {
        String targetSearch = "";
        if(StringUtils.isNotBlank(search)){
            String[] searchs = StringUtils.split(search, " ");
            //字符串拼接
            targetSearch  = Arrays.stream(searchs).collect(Collectors.joining("|"));
        }


        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(targetSearch);

        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        paginationDTO.setPagination(totalCount, page, size);

        if(page < 1){
            page = 1;
        }
        if(page > paginationDTO.getTotalPage()){
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page - 1);

        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//根据变量名称通过反射 注入复制属性
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
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
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
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

    public void incView(Long id) {
        //高并发下 会出现读 脏数据
//        Question question = questionMapper.selectByPrimaryKey((long) id);
//        Question updateQuestion = new Question();
//        updateQuestion.setId((long)id);
//        updateQuestion.setViewCount(question.getViewCount() + 1);
//        questionMapper.updateByPrimaryKeySelective(updateQuestion);

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
    public List<QuestionDTO> listByRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        //字符串拼接
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));

        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
