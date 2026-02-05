package com.example.rbac.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ResourceQueryDTO {
    /**
     * 资源编码模糊查询
     */
    @Size(max = 128, message = "资源编码长度不能超过128")
    private String resourceCodeLike;

    /**
     * 资源名称模糊查询
     */
    @Size(max = 64, message = "资源名称长度不能超过64")
    private String resourceNameLike;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 父资源ID
     */
    private Long parentId;

    /**
     * 页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 100, message = "页大小不能超过100")
    private Integer pageSize = 10;

    public @Size(max = 128, message = "资源编码长度不能超过128") String getResourceCodeLike() {
        return resourceCodeLike;
    }

    public void setResourceCodeLike(@Size(max = 128, message = "资源编码长度不能超过128") String resourceCodeLike) {
        this.resourceCodeLike = resourceCodeLike;
    }

    public @Size(max = 64, message = "资源名称长度不能超过64") String getResourceNameLike() {
        return resourceNameLike;
    }

    public void setResourceNameLike(@Size(max = 64, message = "资源名称长度不能超过64") String resourceNameLike) {
        this.resourceNameLike = resourceNameLike;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public @Min(value = 1, message = "页码不能小于1") Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(@Min(value = 1, message = "页码不能小于1") Integer pageNum) {
        this.pageNum = pageNum;
    }

    public @Min(value = 1, message = "页大小不能小于1") @Max(value = 100, message = "页大小不能超过100") Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(@Min(value = 1, message = "页大小不能小于1") @Max(value = 100, message = "页大小不能超过100") Integer pageSize) {
        this.pageSize = pageSize;
    }
}
