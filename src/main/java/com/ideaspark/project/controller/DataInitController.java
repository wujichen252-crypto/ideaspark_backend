package com.ideaspark.project.controller;

import com.ideaspark.project.util.ResponseUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 数据初始化控制器
 * 用于初始化系统基础数据
 */
@RestController
@RequestMapping("/api/admin/init")
public class DataInitController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 初始化社区圈子数据
     * 会清空现有圈子数据并插入20个完整的圈子
     */
    @PostMapping("/community-groups")
    @Transactional
    public ResponseEntity<?> initCommunityGroups() {
        try {
            // 获取第一个用户作为创建者
            Long creatorId = getFirstUserId();
            if (creatorId == null) {
                return ResponseUtil.error("系统中没有用户，请先创建用户", 400);
            }

            // 清空圈子成员表（外键约束）
            entityManager.createNativeQuery("DELETE FROM community_group_members").executeUpdate();
            
            // 清空圈子表
            entityManager.createNativeQuery("DELETE FROM community_groups").executeUpdate();

            // 插入20个圈子
            String[][] groups = {
                {"Vue.js 技术交流", "vue", "Vue.js 生态系统讨论，包含 Vue 3、Vite、Pinia 等技术话题"},
                {"React 开发社区", "react", "React、Next.js、React Native 等前端技术交流"},
                {"TypeScript 进阶", "typescript", "TypeScript 类型系统、最佳实践和进阶技巧分享"},
                {"Java 后端开发", "java", "Spring Boot、Spring Cloud、微服务架构讨论"},
                {"Go 语言编程", "golang", "Go 语言学习、项目实战和性能优化"},
                {"Python 数据科学", "python", "Python、数据分析、机器学习、人工智能"},
                {"Node.js 全栈", "nodejs", "Node.js、Express、NestJS 服务端开发"},
                {"Rust 系统编程", "rust", "Rust 语言、系统编程、WebAssembly 开发"},
                {"前端工程化", "frontend", "Webpack、Vite、esbuild、前端构建工具"},
                {"CSS 艺术与设计", "css", "CSS3、动画、响应式设计、UI/UX"},
                {"移动端开发", "mobile", "iOS、Android、Flutter、跨平台开发"},
                {"小程序开发", "miniprogram", "微信小程序、支付宝小程序、UniApp"},
                {"数据库技术", "database", "MySQL、PostgreSQL、MongoDB、Redis"},
                {"DevOps 与云原生", "devops", "Docker、K8s、CI/CD、DevOps 实践"},
                {"Linux 与服务器", "linux", "Linux 运维、Shell 脚本、服务器管理"},
                {"人工智能 AI", "ai", "ChatGPT、大模型、AIGC、AI 应用开发"},
                {"区块链技术", "blockchain", "Web3、智能合约、区块链开发"},
                {"程序员职场", "career", "职业规划、面试经验、简历优化、职场交流"},
                {"开源项目贡献", "opensource", "开源项目推荐、贡献指南、协作开发"},
                {"远程工作交流", "remote", "远程办公、自由职业、数字游民生活方式"}
            };

            String[] iconUrls = {
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/typescript/typescript-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/go/go-original-wordmark.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nodejs/nodejs-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/rust/rust-plain.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/webpack/webpack-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/css3/css3-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/flutter/flutter-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/wechat/wechat-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/linux/linux-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tensorflow/tensorflow-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/ethereum/ethereum-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/slack/slack-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg",
                "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/chrome/chrome-original.svg"
            };

            String[] coverUrls = {
                "https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1614624532983-4ce03382d63d?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1627398242454-45a1465c2479?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1629654297299-c8506221ca97?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=600&h=200&fit=crop",
                "https://images.unsplash.com/photo-1593642632823-8f78536788c6?w=600&h=200&fit=crop"
            };

            LocalDateTime now = LocalDateTime.now();
            int insertedCount = 0;

            for (int i = 0; i < groups.length; i++) {
                String groupId = String.format("grp-%03d", i + 1);
                String name = groups[i][0];
                String keyword = groups[i][1];
                String description = groups[i][2];
                String iconUrl = iconUrls[i];
                String coverUrl = coverUrls[i];

                String sql = "INSERT INTO community_groups (id, name, keyword, description, icon_url, cover_url, created_by, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                entityManager.createNativeQuery(sql)
                        .setParameter(1, groupId)
                        .setParameter(2, name)
                        .setParameter(3, keyword)
                        .setParameter(4, description)
                        .setParameter(5, iconUrl)
                        .setParameter(6, coverUrl)
                        .setParameter(7, creatorId)
                        .setParameter(8, now)
                        .setParameter(9, now)
                        .executeUpdate();

                // 创建者自动加入圈子
                String memberSql = "INSERT INTO community_group_members (id, group_id, user_id, role, joined_at) VALUES (?, ?, ?, ?, ?)";
                entityManager.createNativeQuery(memberSql)
                        .setParameter(1, UUID.randomUUID().toString())
                        .setParameter(2, groupId)
                        .setParameter(3, creatorId)
                        .setParameter(4, "admin")
                        .setParameter(5, now)
                        .executeUpdate();

                insertedCount++;
            }

            return ResponseUtil.success(Map.of(
                "insertedCount", insertedCount,
                "message", "成功初始化 " + insertedCount + " 个圈子"
            ));

        } catch (Exception e) {
            return ResponseUtil.error("初始化圈子数据失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 获取第一个用户的ID
     */
    private Long getFirstUserId() {
        try {
            List<?> results = entityManager.createNativeQuery(
                "SELECT id FROM users ORDER BY id ASC LIMIT 1"
            ).getResultList();
            
            if (results.isEmpty()) {
                return null;
            }
            
            Object result = results.get(0);
            if (result instanceof BigInteger) {
                return ((BigInteger) result).longValue();
            } else if (result instanceof Number) {
                return ((Number) result).longValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
