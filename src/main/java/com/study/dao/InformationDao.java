package com.study.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.domain.Information;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InformationDao extends BaseMapper<Information> {
}
