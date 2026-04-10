-- 插入测试用户
-- 邮箱: test@example.com
-- 密码: 123456
-- 密码使用 BCrypt 加密: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgN0sZ7sTL9UqLqHgDHJxO4lqXYW

INSERT INTO users (
    username, 
    email, 
    password_hash, 
    avatar, 
    role, 
    bio, 
    is_hide, 
    is_notifisys, 
    is_notiftrends, 
    is_notifipost,
    likes_count,
    followers_count,
    following_count,
    state
) VALUES (
    '测试用户', 
    'test@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgN0sZ7sTL9UqLqHgDHJxO4lqXYW', 
    'https://api.dicebear.com/7.x/avataaars/svg?seed=test',
    'USER',
    '这是一个测试用户账号',
    0,
    1,
    1,
    0,
    0,
    0,
    0,
    'N'
) ON DUPLICATE KEY UPDATE 
    password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgN0sZ7sTL9UqLqHgDHJxO4lqXYW';

-- 插入管理员用户
-- 邮箱: admin@example.com
-- 密码: admin123
-- 密码使用 BCrypt 加密: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgN0sZ7sTL9UqLqHgDHJxO4lqXYW

INSERT INTO users (
    username, 
    email, 
    password_hash, 
    avatar, 
    role, 
    bio, 
    is_hide, 
    is_notifisys, 
    is_notiftrends, 
    is_notifipost,
    likes_count,
    followers_count,
    following_count,
    state
) VALUES (
    '管理员', 
    'admin@example.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgN0sZ7sTL9UqLqHgDHJxO4lqXYW', 
    'https://api.dicebear.com/7.x/avataaars/svg?seed=admin',
    'ADMIN',
    '系统管理员账号',
    0,
    1,
    1,
    0,
    0,
    0,
    0,
    'N'
) ON DUPLICATE KEY UPDATE 
    password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgN0sZ7sTL9UqLqHgDHJxO4lqXYW';
