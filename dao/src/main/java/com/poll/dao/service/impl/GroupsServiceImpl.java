package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.GroupsMapper;
import com.poll.dao.service.GroupsService;
import com.poll.entity.StaffGpEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupsServiceImpl extends ServiceImpl<GroupsMapper,StaffGpEntity> implements GroupsService {
}
