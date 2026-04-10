-- ============================================
-- 社区圈子初始化数据
-- 执行前请确保已清空 community_groups 和 community_group_members 表
-- ============================================

-- 先清空圈子成员表（外键约束）
DELETE FROM community_group_members;

-- 清空圈子表
DELETE FROM community_groups;

-- 插入圈子数据
-- 注意：created_by 字段需要根据实际用户ID修改，这里使用 ID=1 作为默认创建者
INSERT INTO community_groups (id, name, keyword, description, icon_url, cover_url, created_by, created_at, updated_at) VALUES
-- 技术类圈子
('grp-001', 'Vue.js 技术交流', 'vue', 'Vue.js 生态系统讨论，包含 Vue 3、Vite、Pinia 等技术话题', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg',
 'https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-002', 'React 开发社区', 'react', 'React、Next.js、React Native 等前端技术交流', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg',
 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-003', 'TypeScript 进阶', 'typescript', 'TypeScript 类型系统、最佳实践和进阶技巧分享', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/typescript/typescript-original.svg',
 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-004', 'Java 后端开发', 'java', 'Spring Boot、Spring Cloud、微服务架构讨论', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg',
 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-005', 'Go 语言编程', 'golang', 'Go 语言学习、项目实战和性能优化', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/go/go-original-wordmark.svg',
 'https://images.unsplash.com/photo-1614624532983-4ce03382d63d?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-006', 'Python 数据科学', 'python', 'Python、数据分析、机器学习、人工智能', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg',
 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-007', 'Node.js 全栈', 'nodejs', 'Node.js、Express、NestJS 服务端开发', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nodejs/nodejs-original.svg',
 'https://images.unsplash.com/photo-1627398242454-45a1465c2479?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-008', 'Rust 系统编程', 'rust', 'Rust 语言、系统编程、WebAssembly 开发', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/rust/rust-plain.svg',
 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

-- 前端技术
('grp-009', '前端工程化', 'frontend', 'Webpack、Vite、esbuild、前端构建工具', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/webpack/webpack-original.svg',
 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-010', 'CSS 艺术与设计', 'css', 'CSS3、动画、响应式设计、UI/UX', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/css3/css3-original.svg',
 'https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

-- 移动开发
('grp-011', '移动端开发', 'mobile', 'iOS、Android、Flutter、跨平台开发', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/flutter/flutter-original.svg',
 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-012', '小程序开发', 'miniprogram', '微信小程序、支付宝小程序、UniApp', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/wechat/wechat-original.svg',
 'https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

-- 数据库与运维
('grp-013', '数据库技术', 'database', 'MySQL、PostgreSQL、MongoDB、Redis', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg',
 'https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-014', 'DevOps 与云原生', 'devops', 'Docker、K8s、CI/CD、DevOps 实践', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg',
 'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-015', 'Linux 与服务器', 'linux', 'Linux 运维、Shell 脚本、服务器管理', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/linux/linux-original.svg',
 'https://images.unsplash.com/photo-1629654297299-c8506221ca97?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

-- AI 与新技术
('grp-016', '人工智能 AI', 'ai', 'ChatGPT、大模型、AIGC、AI 应用开发', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tensorflow/tensorflow-original.svg',
 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-017', '区块链技术', 'blockchain', 'Web3、智能合约、区块链开发', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/ethereum/ethereum-original.svg',
 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

-- 职业发展
('grp-018', '程序员职场', 'career', '职业规划、面试经验、简历优化、职场交流', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/slack/slack-original.svg',
 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-019', '开源项目贡献', 'opensource', '开源项目推荐、贡献指南、协作开发', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg',
 'https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=600&h=200&fit=crop',
 1, NOW(), NOW()),

('grp-020', '远程工作交流', 'remote', '远程办公、自由职业、数字游民生活方式', 
 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/chrome/chrome-original.svg',
 'https://images.unsplash.com/photo-1593642632823-8f78536788c6?w=600&h=200&fit=crop',
 1, NOW(), NOW());

-- 为创建者自动加入所有圈子作为管理员
INSERT INTO community_group_members (id, group_id, user_id, role, joined_at)
SELECT 
    UUID() as id,
    id as group_id,
    1 as user_id,
    'admin' as role,
    NOW() as joined_at
FROM community_groups;

-- 查看插入结果
SELECT g.*, u.username as creator_name 
FROM community_groups g
LEFT JOIN users u ON g.created_by = u.id;
