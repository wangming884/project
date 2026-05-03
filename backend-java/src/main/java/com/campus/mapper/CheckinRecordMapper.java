package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 签到记录 Mapper 接口
 * 
 * @author Campus Platform Team
 */
@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {
}
