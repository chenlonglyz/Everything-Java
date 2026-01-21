-- 初始化超级管理员数据
INSERT INTO `rbac_role` (`role_code`, `role_name`, `description`, `status`)
VALUES ('SUPER_ADMIN', '超级管理员', '拥有所有权限', 1);

INSERT INTO `rbac_user` (`user_id`, `username`, `status`, `is_super_admin`)
VALUES (1, 'admin', 1, 1);

INSERT INTO `rbac_user_role` (`user_id`, `role_id`, `status`)
VALUES (1, 1, 1);

INSERT INTO `rbac_resource` (`resource_code`, `resource_name`, `resource_type`, `status`, `level`, `full_path`)
VALUES ('*', '所有资源', 'API', 1, 1, '*');

INSERT INTO `rbac_role_resource` (`role_id`, `resource_id`)
VALUES (1, 1);