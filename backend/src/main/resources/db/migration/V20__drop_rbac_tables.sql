-- Drop RBAC join tables first (foreign key dependents)
DROP TABLE IF EXISTS group_roles;
DROP TABLE IF EXISTS role_permissions;

-- Drop RBAC core tables
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS permissions;
