package com.bbu.springstudy.community.service;

import com.bbu.springstudy.community.dto.NotificationDTO;
import com.bbu.springstudy.community.dto.PaginationDTO;
import com.bbu.springstudy.community.enums.NotificationStatusEnum;
import com.bbu.springstudy.community.enums.NotificationTypeEnum;
import com.bbu.springstudy.community.exception.CustomizeErrorCode;
import com.bbu.springstudy.community.exception.CustomizeException;
import com.bbu.springstudy.community.mapper.NotificationMapper;
import com.bbu.springstudy.community.model.Notification;
import com.bbu.springstudy.community.model.NotificationExample;
import com.bbu.springstudy.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);

        Integer totalCount = Math.toIntExact(notificationMapper.countByExample(notificationExample));
        paginationDTO.setPagination(totalCount, page, size);
        if(page < 1){
            page = 1;
        }
        if(page > paginationDTO.getTotalPage()){
            page = paginationDTO.getTotalPage();
        }
        Integer offset = size * (page - 1);
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));

        if(notifications.size() == 0){
            return paginationDTO;
        }
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setType(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;

    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        long unreadCount = notificationMapper.countByExample(notificationExample);
        return unreadCount;
    }

    public Notification read(Long id, User user) {
        Notification dbNotification = notificationMapper.selectByPrimaryKey(id);
        if(dbNotification == null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }else if(Objects.equals(dbNotification.getReceiver(), user.getId())){

            NotificationExample notificationExample = new NotificationExample();
            notificationExample.createCriteria().andIdEqualTo(dbNotification.getId());

            Notification notification = new Notification();
            notification.setStatus(NotificationStatusEnum.READ.getStatus());
            if(notificationMapper.updateByExampleSelective(notification, notificationExample) != 0){
                dbNotification.setStatus(NotificationStatusEnum.READ.getStatus());
            }
            return dbNotification;
        }else {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
    }

}
