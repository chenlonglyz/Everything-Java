package com.example.rbac.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResourceUpdateDTO {
    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空")
    private Long id;

    /**
     * 资源名称
     */
    @Size(max = 64, message = "资源名称长度不能超过64")
    private String resourceName;

    /**
     * 资源类型：API/PAGE/BUTTON
     */
    private String resourceType;

    /**
     * 父资源ID
     */
    private Long parentId;

    /**
     * 层级（1-3层）
     */
    private Integer level;

    /**
     * 完整路径（如：sys:system:user）
     */
    @Size(max = 512, message = "完整路径长度不能超过512")
    private String fullPath;

    /**
     * HTTP方法：GET/POST/PUT/DELETE
     */
    @Size(max = 16, message = "HTTP方法长度不能超过16")
    private String requestMethod;

    /**
     * 接口路径（如：/api/v1/user/list）
     */
    @Size(max = 256, message = "接口路径长度不能超过256")
    private String requestPath;

    /**
     * 关联的API资源ID，逗号分隔
     */
    @Size(max = 1024, message = "关联API资源ID长度不能超过1024")
    private String apiIds;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    public @NotNull(message = "资源ID不能为空") Long getId() {
        return id;
    }

    public void setId(@NotNull(message = "资源ID不能为空") Long id) {
        this.id = id;
    }

    public @Size(max = 64, message = "资源名称长度不能超过64") String getResourceName() {
        return resourceName;
    }

    public void setResourceName(@Size(max = 64, message = "资源名称长度不能超过64") String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public @Size(max = 512, message = "完整路径长度不能超过512") String getFullPath() {
        return fullPath;
    }

    public void setFullPath(@Size(max = 512, message = "完整路径长度不能超过512") String fullPath) {
        this.fullPath = fullPath;
    }

    public @Size(max = 16, message = "HTTP方法长度不能超过16") String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(@Size(max = 16, message = "HTTP方法长度不能超过16") String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public @Size(max = 256, message = "接口路径长度不能超过256") String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(@Size(max = 256, message = "接口路径长度不能超过256") String requestPath) {
        this.requestPath = requestPath;
    }

    public @Size(max = 1024, message = "关联API资源ID长度不能超过1024") String getApiIds() {
        return apiIds;
    }

    public void setApiIds(@Size(max = 1024, message = "关联API资源ID长度不能超过1024") String apiIds) {
        this.apiIds = apiIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
