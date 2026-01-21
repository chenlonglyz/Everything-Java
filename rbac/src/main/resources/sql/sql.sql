-- 1. 资源表（核心优化：层级/全路径/关联API）
DROP TABLE IF EXISTS `rbac_resource`;
CREATE TABLE `rbac_resource` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
                                 `resource_code` varchar(128) NOT NULL COMMENT '资源编码（如：sys:user:list）',
                                 `resource_name` varchar(64) NOT NULL COMMENT '资源名称',
                                 `resource_type` varchar(32) NOT NULL COMMENT '资源类型：API/PAGE/BUTTON',
                                 `parent_id` bigint DEFAULT 0 COMMENT '父资源ID',
                                 `level` tinyint NOT NULL DEFAULT 1 COMMENT '层级（1-3层）',
                                 `full_path` varchar(512) DEFAULT NULL COMMENT '完整路径（如：sys:system:user）',
                                 `request_method` varchar(16) DEFAULT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE',
                                 `request_path` varchar(256) DEFAULT NULL COMMENT '接口路径（如：/api/v1/user/list）',
                                 `api_ids` varchar(1024) DEFAULT NULL COMMENT '关联的API资源ID，逗号分隔',
                                 `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
                                 `sort` int DEFAULT 0 COMMENT '排序',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_resource_code` (`resource_code`),
                                 KEY `idx_type_status_level` (`resource_type`, `status`, `level`) USING BTREE,
                                 KEY `idx_parent_id_level` (`parent_id`, `level`) USING BTREE,
                                 KEY `idx_full_path` (`full_path`) USING BTREE,
                                 KEY `idx_request_path_method` (`request_path`,`request_method`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RBAC资源表';

-- 2. 角色表（无核心变更，优化索引）
DROP TABLE IF EXISTS `rbac_role`;
CREATE TABLE `rbac_role` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                             `role_code` varchar(64) NOT NULL COMMENT '角色编码（如：ADMIN/OPERATOR）',
                             `role_name` varchar(64) NOT NULL COMMENT '角色名称',
                             `description` varchar(256) DEFAULT NULL COMMENT '角色描述',
                             `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_role_code` (`role_code`),
                             KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RBAC角色表';

-- 3. 角色-资源关联表（优化索引）
DROP TABLE IF EXISTS `rbac_role_resource`;
CREATE TABLE `rbac_role_resource` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `role_id` bigint NOT NULL COMMENT '角色ID',
                                      `resource_id` bigint NOT NULL COMMENT '资源ID',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_role_resource` (`role_id`,`resource_id`),
                                      KEY `idx_role_id_resource_id` (`role_id`, `resource_id`) USING BTREE,
                                      KEY `idx_resource_id` (`resource_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-资源关联表';

-- 4. 用户-角色关联表（优化索引）
DROP TABLE IF EXISTS `rbac_user_role`;
CREATE TABLE `rbac_user_role` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `user_id` bigint NOT NULL COMMENT '用户ID（用户中心生成）',
                                  `role_id` bigint NOT NULL COMMENT '角色ID',
                                  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
                                  KEY `idx_user_id_status_role_id` (`user_id`, `status`, `role_id`) USING BTREE,
                                  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 5. RBAC用户表（无核心变更）
DROP TABLE IF EXISTS `rbac_user`;
CREATE TABLE `rbac_user` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `user_id` bigint NOT NULL COMMENT '用户中心生成的用户ID',
                             `username` varchar(64) NOT NULL COMMENT '用户名（冗余，便于展示）',
                             `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
                             `is_super_admin` tinyint NOT NULL DEFAULT 0 COMMENT '是否超级管理员：0否 1是',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_user_id` (`user_id`),
                             KEY `idx_username` (`username`) USING BTREE,
                             KEY `idx_is_super_admin` (`is_super_admin`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RBAC用户表（仅权限相关）';

-- 6. 缓存刷新日志表（优化索引）
DROP TABLE IF EXISTS `rbac_cache_log`;
CREATE TABLE `rbac_cache_log` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `cache_type` varchar(32) NOT NULL COMMENT '缓存类型：USER_PERM/RESOURCE_ALL',
                                  `related_id` bigint DEFAULT NULL COMMENT '关联ID（如用户ID）',
                                  `operate_type` varchar(16) NOT NULL COMMENT '操作类型：REFRESH/DELETE',
                                  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                  `is_processed` tinyint NOT NULL DEFAULT 0 COMMENT '是否已处理：0否 1是',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_cache_type_related` (`cache_type`,`related_id`) USING BTREE,
                                  KEY `idx_operate_time_processed` (`operate_time`, `is_processed`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RBAC缓存刷新日志';

-- 7. 资源层级触发器（限制嵌套深度+自动生成full_path）
DELIMITER //
DROP TRIGGER IF EXISTS `trg_rbac_resource_insert`//
CREATE TRIGGER `trg_rbac_resource_insert` BEFORE INSERT ON `rbac_resource`
    FOR EACH ROW
BEGIN
    -- 1. 初始化层级和全路径
    IF NEW.`parent_id` = 0 THEN
    SET NEW.`level` = 1;
    SET NEW.`full_path` = NEW.`resource_code`;
    ELSE
    -- 查询父级信息
    SELECT `resource_type`, `level`, `full_path` INTO @parent_type, @parent_level, @parent_full_path
    FROM `rbac_resource` WHERE `id` = NEW.`parent_id`;

    -- 2. 校验嵌套深度
    IF NEW.`resource_type` = 'PAGE' THEN
      SET NEW.`level` = @parent_level + 1;
      IF NEW.`level` > 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '页面资源嵌套深度不能超过3层';
END IF;
ELSEIF NEW.`resource_type` = 'BUTTON' THEN
      IF @parent_type NOT IN ('PAGE', 'API') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '按钮资源只能挂在页面或API下';
END IF;
      SET NEW.`level` = @parent_level + 1;
      IF NEW.`level` > 2 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '按钮资源嵌套深度不能超过2层';
END IF;
    ELSEIF NEW.`resource_type` = 'API' THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'API资源不能设置父级（parent_id必须为0）';
END IF;

    -- 3. 生成全路径
    SET NEW.`full_path` = CONCAT(@parent_full_path, ':', NEW.`resource_code`);
END IF;

  -- 4. API资源强制校验
  IF NEW.`resource_type` = 'API' THEN
    IF NEW.`request_method` IS NULL OR NEW.`request_path` IS NULL THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'API资源必须填写request_method和request_path';
END IF;
END IF;
END //

DROP TRIGGER IF EXISTS `trg_rbac_resource_update`//
CREATE TRIGGER `trg_rbac_resource_update` BEFORE UPDATE ON `rbac_resource`
    FOR EACH ROW
BEGIN
    -- 父级变更时重新计算层级和全路径
    IF NEW.`parent_id` != OLD.`parent_id` THEN
    IF NEW.`parent_id` = 0 THEN
      SET NEW.`level` = 1;
      SET NEW.`full_path` = NEW.`resource_code`;
    ELSE
    SELECT `level`, `full_path` INTO @parent_level, @parent_full_path
    FROM `rbac_resource` WHERE `id` = NEW.`parent_id`;
    SET NEW.`level` = @parent_level + 1;
      SET NEW.`full_path` = CONCAT(@parent_full_path, ':', NEW.`resource_code`);
END IF;
END IF;
END //
DELIMITER ;

