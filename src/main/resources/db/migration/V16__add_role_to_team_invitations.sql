ALTER TABLE team_invitations
    ADD COLUMN role VARCHAR(20) NULL COMMENT '被邀请成员角色(admin/member/visitor)';

