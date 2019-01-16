package com.poll.dao.mapper; 

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.poll.entity.CompanyEntity;

@Mapper
@Repository("companyMapper")
public interface CompanyMapper extends BaseMapper<CompanyEntity> {

}