package com.bbu.springstudy.community.service;

import com.bbu.springstudy.community.dto.CommentDTO;
import com.bbu.springstudy.community.enums.CommentTypeEnum;
import com.bbu.springstudy.community.enums.NotificationStatusEnum;
import com.bbu.springstudy.community.enums.NotificationTypeEnum;
import com.bbu.springstudy.community.exception.CustomizeErrorCode;
import com.bbu.springstudy.community.exception.CustomizeException;
import com.bbu.springstudy.community.mapper.*;
import com.bbu.springstudy.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator){
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            //父评论是否存在
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加评论数

            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);

            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            //增加通知
            if(!Objects.equals(dbComment.getCommentator(),comment.getCommentCount())){
                Notification notification = createNotify(comment, dbComment.getCommentator(),
                        commentator.getName(),question.getTitle(), NotificationTypeEnum.REPLY_COMMENT,
                        question.getId());
                notificationMapper.insert(notification);
            }

        }else {
            //回复问题
            //问题是否存在
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            //增加通知
            if(!Objects.equals(question.getCreator(),comment.getCommentator())){
                Notification notification = createNotify(comment,question.getCreator(),
                        commentator.getName(),question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
                notificationMapper.insert(notification);
            }
        }
    }
    //创建通知
    private Notification createNotify(Comment comment, Long receiver, String notifierName, String outerTitle,
                                      NotificationTypeEnum notificationType,Long outerId) {
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        return notification;
    }

    /**
     *  通过id 获取 评论
     * @param id
     * @param type
     * @return
     */
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
            commentExample.createCriteria()
                    .andParentIdEqualTo(id)
                    .andTypeEqualTo(type.getType());
        //排序
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if(comments.size() == 0){
            return new ArrayList<>();
        }
        // stream().map（） >> 遍历一下集合 遍历 comment  返回一个 comment.getCommentator();
        //  .collect()结果放在集合里放在Collectors.toSet(); 放在一个set集合里 放在set 里 防止存储重复评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        UserExample userExample = new UserExample();

        userExample.createCriteria()
                .andIdIn(new ArrayList<>(commentators));
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment .getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
