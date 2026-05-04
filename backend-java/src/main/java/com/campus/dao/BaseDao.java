package com.campus.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 数据访问层基础接口
 * 提供通用的数据库操作方法
 * 
 * @param <T> 实体类型
 * @param <M> Mapper类型
 * @author Campus Platform Team
 */
public interface BaseDao<T, M extends BaseMapper<T>> {
    
    /**
     * 获取Mapper实例
     */
    M getMapper();
    
    /**
     * 根据ID查询
     */
    default T findById(Long id) {
        return getMapper().selectById(id);
    }
    
    /**
     * 查询所有
     */
    default List<T> findAll() {
        return getMapper().selectList(null);
    }
    
    /**
     * 根据条件查询
     */
    default List<T> findByCondition(LambdaQueryWrapper<T> wrapper) {
        return getMapper().selectList(wrapper);
    }
    
    /**
     * 根据条件查询单个
     */
    default T findOneByCondition(LambdaQueryWrapper<T> wrapper) {
        return getMapper().selectOne(wrapper);
    }
    
    /**
     * 分页查询
     */
    default Page<T> findPage(Page<T> page, LambdaQueryWrapper<T> wrapper) {
        return getMapper().selectPage(page, wrapper);
    }
    
    /**
     * 统计数量
     */
    default Long count(LambdaQueryWrapper<T> wrapper) {
        return getMapper().selectCount(wrapper);
    }
    
    /**
     * 插入
     */
    default int insert(T entity) {
        return getMapper().insert(entity);
    }
    
    /**
     * 根据ID更新
     */
    default int updateById(T entity) {
        return getMapper().updateById(entity);
    }
    
    /**
     * 根据ID删除
     */
    default int deleteById(Long id) {
        return getMapper().deleteById(id);
    }
    
    /**
     * 根据条件删除
     */
    default int deleteByCondition(LambdaQueryWrapper<T> wrapper) {
        return getMapper().delete(wrapper);
    }
    
    /**
     * 批量插入
     */
    default int batchInsert(List<T> entities) {
        int count = 0;
        for (T entity : entities) {
            count += insert(entity);
        }
        return count;
    }
    
    /**
     * 判断是否存在
     */
    default boolean exists(LambdaQueryWrapper<T> wrapper) {
        return count(wrapper) > 0;
    }
}
