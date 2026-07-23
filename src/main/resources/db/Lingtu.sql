CREATE DATABASE IF NOT EXISTS lingtu_agent DEFAULT CHARSET utf8mb4;
USE lingtu_agent;
-- 用户表
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                                    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
                                    email VARCHAR(100) COMMENT '邮箱',
                                    nickname VARCHAR(50) COMMENT '昵称',
                                    avatar VARCHAR(500) COMMENT '头像URL',
                                    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色(USER/ADMIN)',
                                    status INT DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
                                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
-- 城市表
CREATE TABLE IF NOT EXISTS city (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL COMMENT '城市名称',
                                    province VARCHAR(100) COMMENT '省份',
                                    description TEXT COMMENT '城市描述',
                                    image_url VARCHAR(500) COMMENT '图片地址',
                                    sort_order INT DEFAULT 0 COMMENT '排序',
                                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                    INDEX idx_name (name),
                                    INDEX idx_province (province)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市表';
-- 景点表
    CREATE TABLE IF NOT EXISTS scenic_spot (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(200) NOT NULL COMMENT '景点名称',
                                           city_id BIGINT COMMENT '城市ID',
                                           description TEXT COMMENT '景点描述',
                                           address VARCHAR(500) COMMENT '地址',
                                           image_url VARCHAR(500) COMMENT '图片地址',
                                           rating DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
                                           price DECIMAL(10,2) DEFAULT 0.00 COMMENT '门票价格',
                                           opening_hours VARCHAR(100) COMMENT '开放时间',
                                           tags VARCHAR(500) COMMENT '标签',
                                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                           INDEX idx_city_id (city_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';
-- 酒店表
CREATE TABLE IF NOT EXISTS hotel (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(200) NOT NULL COMMENT '酒店名称',
                                     city_id BIGINT COMMENT '城市ID',
                                     description TEXT COMMENT '酒店描述',
                                     address VARCHAR(500) COMMENT '地址',
                                     image_url VARCHAR(500) COMMENT '图片地址',
                                     rating DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
                                    star_rating INT DEFAULT 3 COMMENT '星级(1-5)',
                                    price_range VARCHAR(50) COMMENT '价格范围',
                                     facilities VARCHAR(500) COMMENT '设施',
                                     contact_phone VARCHAR(50) COMMENT '联系电话',
                                     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                     INDEX idx_city_id (city_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店表';
-- 美食表
CREATE TABLE IF NOT EXISTS food (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(200) NOT NULL COMMENT '美食名称',
                                    city_id BIGINT COMMENT '城市ID',
                                    description TEXT COMMENT '美食描述',
                                    address VARCHAR(500) COMMENT '地址',
                                    image_url VARCHAR(500) COMMENT '图片地址',
                                    rating DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
                                    price_range VARCHAR(50) COMMENT '价格范围',
                                    category VARCHAR(100) COMMENT '分类',
                                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                    INDEX idx_city_id (city_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='美食表';
-- 行程表
CREATE TABLE IF NOT EXISTS itinerary (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         user_id BIGINT COMMENT '用户ID',
                                         title VARCHAR(200) NOT NULL COMMENT '行程标题',
                                         city_id BIGINT COMMENT '城市ID',
                                         start_date DATE COMMENT '开始日期',
                                         end_date DATE COMMENT '结束日期',
                                         days INT DEFAULT 1 COMMENT '天数',
                                    budget DECIMAL(10,2) DEFAULT 0.00 COMMENT '预算',
                                    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态(DRAFT-草稿,PUBLISHED-已发布)',
                                    description TEXT COMMENT '行程描述',
                                    content LONGTEXT COMMENT '行程内容',
                                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                         INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程表';
-- 行程日表
CREATE TABLE IF NOT EXISTS itinerary_day (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             itinerary_id BIGINT COMMENT '行程ID',
                                             day_number INT COMMENT '第几天',
                                             description TEXT COMMENT '描述',
                                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                             INDEX idx_itinerary_id (itinerary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程日表';
-- 行程项目表
CREATE TABLE IF NOT EXISTS itinerary_item (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              itinerary_day_id BIGINT COMMENT '行程日ID',
                                              item_type VARCHAR(50) COMMENT '项目类型',
                                              item_id BIGINT COMMENT '项目ID',
                                              sort_order INT DEFAULT 0 COMMENT '排序',
                                              note VARCHAR(500) COMMENT '备注',
                                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                              INDEX idx_itinerary_day_id (itinerary_day_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程项目表';
-- 聊天会话表
CREATE TABLE IF NOT EXISTS chat_session (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            user_id BIGINT COMMENT '用户ID',
                                            title VARCHAR(200) DEFAULT '新对话' COMMENT '会话标题',
                                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';
-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            session_id BIGINT COMMENT '会话ID',
                                            user_id BIGINT COMMENT '用户ID',
                                            role VARCHAR(20) COMMENT '角色',
                                            content TEXT COMMENT '内容',
                                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                            INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';
-- 知识库表
CREATE TABLE IF NOT EXISTS `knowledge_doc` (
                                                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                                    `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                                    `title` VARCHAR(255) NOT NULL COMMENT '文档标题',
                                                    `file_type` VARCHAR(20) NOT NULL COMMENT '文件类型：PDF/WORD/MARKDOWN',
                                                    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
                                                    `content` TEXT COMMENT '提取的文本内容',
                                                    `chunk_count` INT DEFAULT 0 COMMENT '分块数量',
                                                    `status` TINYINT DEFAULT 0 COMMENT '状态：0-处理中 1-已入库 2-失败',
                                                    `error_msg` VARCHAR(500) COMMENT '错误信息',
                                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                                    INDEX `idx_user_id` (`user_id`),
                                                    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库文档表';
# CREATE TABLE IF NOT EXISTS knowledge_base (
#                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
#                                               user_id BIGINT COMMENT '用户ID',
#                                               title VARCHAR(200) COMMENT '标题',
#                                               file_name VARCHAR(200) COMMENT '文件名',
#                                               file_type VARCHAR(50) COMMENT '文件类型',
#                                               content LONGTEXT COMMENT '内容',
#                                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#                                               update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#                                               deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
#                                               INDEX idx_user_id (user_id)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';
#  DROP TABLE IF EXISTS knowledge_document;
-- Prompt模板表
CREATE TABLE IF NOT EXISTS `prompt_template` (
                                                 `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                                 `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
                                                 `category` VARCHAR(50) COMMENT '场景分类：chat/planner/rag',
                                                 `template` TEXT NOT NULL COMMENT 'Prompt模板内容',
                                                 `variables` JSON COMMENT '变量列表（JSON数组）',
                                                 `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
                                                 `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 INDEX `idx_category` (`category`),
                                                 INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Prompt模板表';