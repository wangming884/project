package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.PointsHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分历史 Mapper 接口
 * 
 * @author Campus Platform Team
 */
@Mapper
public interface PointsHistoryMapper extends BaseMapper<PointsHistory> {
}
