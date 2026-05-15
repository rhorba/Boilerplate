-- Seed admin user attributes
INSERT INTO user_attributes (user_id, attribute_key, attribute_value, created_at, updated_at, version)
SELECT id, 'role', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM users WHERE username = 'admin';

-- ============================================================
-- ADMIN policies: full access (no conditions = always permit)
-- ============================================================
INSERT INTO policies (name, description, effect, resource, action, enabled, created_at, updated_at, version)
VALUES
    ('admin-user-read',   'Admins can read users',   'PERMIT', 'USER',  'READ',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-user-create', 'Admins can create users', 'PERMIT', 'USER',  'CREATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-user-update', 'Admins can update users', 'PERMIT', 'USER',  'UPDATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-user-delete', 'Admins can delete users', 'PERMIT', 'USER',  'DELETE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-user-manage', 'Admins can manage users', 'PERMIT', 'USER',  'MANAGE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-group-read',   'Admins can read groups',   'PERMIT', 'GROUP', 'READ',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-group-create', 'Admins can create groups', 'PERMIT', 'GROUP', 'CREATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-group-update', 'Admins can update groups', 'PERMIT', 'GROUP', 'UPDATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-group-delete', 'Admins can delete groups', 'PERMIT', 'GROUP', 'DELETE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-group-manage', 'Admins can manage groups', 'PERMIT', 'GROUP', 'MANAGE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-policy-manage', 'Admins can manage policies', 'PERMIT', 'POLICY', 'MANAGE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-audit-read',   'Admins can read audit logs', 'PERMIT', 'AUDIT_LOG', 'READ', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('admin-system-manage', 'Admins have full system access', 'PERMIT', 'SYSTEM', 'MANAGE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- Conditions: all admin policies require role=ADMIN
INSERT INTO policy_conditions (policy_id, subject, attribute_key, operator, attribute_value, created_at, updated_at, version)
SELECT p.id, 'USER', 'role', 'EQUALS', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM policies p
WHERE p.name IN (
    'admin-user-read', 'admin-user-create', 'admin-user-update', 'admin-user-delete', 'admin-user-manage',
    'admin-group-read', 'admin-group-create', 'admin-group-update', 'admin-group-delete', 'admin-group-manage',
    'admin-policy-manage', 'admin-audit-read', 'admin-system-manage'
);

-- ============================================================
-- MODERATOR policies: user read/manage + group read
-- ============================================================
INSERT INTO policies (name, description, effect, resource, action, enabled, created_at, updated_at, version)
VALUES
    ('moderator-user-read',   'Moderators can read users',   'PERMIT', 'USER',  'READ',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('moderator-user-update', 'Moderators can update users', 'PERMIT', 'USER',  'UPDATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('moderator-group-read',  'Moderators can read groups',  'PERMIT', 'GROUP', 'READ',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO policy_conditions (policy_id, subject, attribute_key, operator, attribute_value, created_at, updated_at, version)
SELECT p.id, 'USER', 'role', 'EQUALS', 'MODERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM policies p
WHERE p.name IN ('moderator-user-read', 'moderator-user-update', 'moderator-group-read');

-- ============================================================
-- USER policies: read own data only
-- ============================================================
INSERT INTO policies (name, description, effect, resource, action, enabled, created_at, updated_at, version)
VALUES
    ('user-self-read', 'Users can read user data', 'PERMIT', 'USER', 'READ', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO policy_conditions (policy_id, subject, attribute_key, operator, attribute_value, created_at, updated_at, version)
SELECT p.id, 'USER', 'role', 'IN', 'USER,MODERATOR,ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM policies p
WHERE p.name = 'user-self-read';
