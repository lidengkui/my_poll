package com.poll.dao.service.impl; 

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.poll.dao.mapper.CompanyMapper;
import com.poll.dao.service.CompanyService;
import com.poll.entity.CompanyEntity;

@Service("companyService")
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyEntity> implements CompanyService {

}