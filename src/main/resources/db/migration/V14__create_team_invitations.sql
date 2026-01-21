CREATE TABLE team_invitations (
    id VARCHAR(36) PRIMARY KEY COMMENT '邀请唯一标识',
    team_id VARCHAR(36) NOT NULL COMMENT '团队 ID',
    inviter_id BIGINT NOT NULL COMMENT '邀请人用户 ID',
    invitee_id BIGINT NULL COMMENT '被邀请人用户 ID（可为空，支持未注册邮箱）',
    invitee_email VARCHAR(100) NULL COMMENT '被邀请邮箱',
    token VARCHAR(64) NOT NULL UNIQUE COMMENT '一次性邀请令牌',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/ACCEPTED/EXPIRED/REVOKED)',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_team_invitations_team_id FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_team_invitations_inviter_id FOREIGN KEY (inviter_id) REFERENCES users(id),
    CONSTRAINT fk_team_invitations_invitee_id FOREIGN KEY (invitee_id) REFERENCES users(id)
) COMMENT '团队邀请表';

CREATE INDEX idx_team_invitations_team_id ON team_invitations(team_id);
CREATE INDEX idx_team_invitations_inviter_id ON team_invitations(inviter_id);
CREATE INDEX idx_team_invitations_invitee_id ON team_invitations(invitee_id);
