package com.example.rbac.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbac.entity.Resource;
import org.apache.ibatis.annotations.Param;

@Repository
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 分页查询资源列表（优化查询性能，使用索引）
     */
    IPage<Resource> selectResourcePage(Page<Resource> page, @Param("params") Map<String, Object> params);

    /**
     * 根据角色ID查询资源列表（权限校验核心，优化查询）
     */
    List<Resource> selectResourceByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据用户ID查询权限编码（缓存热点数据）
     */
    List<String> selectPermCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询资源树形结构（递归查询优化）
     */
    List<Resource> selectResourceTree(@Param("parentId") Long parentId);
}
