# ABAC Browser Test Scenarios

**Base URLs**
- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

**Seeded accounts**
- Admin: `admin` / `admin123` → has `user_attribute: role=ADMIN`
- New registrations start with **zero permissions** until an admin sets their `role` attribute

---

## S-01 — Admin login, full UI

- [ ] Open `http://localhost:4200/login`
- [ ] Enter `admin` / `admin123` → click Login
- [ ] Redirected to `/dashboard`
- [ ] Open DevTools → Network → find `POST /auth/login` response
- [ ] `user.effectivePermissions` contains all 13 strings:
  ```
  USER:READ, USER:CREATE, USER:UPDATE, USER:DELETE, USER:MANAGE,
  GROUP:READ, GROUP:CREATE, GROUP:UPDATE, GROUP:DELETE, GROUP:MANAGE,
  POLICY:MANAGE, AUDIT_LOG:READ, SYSTEM:MANAGE
  ```
- [ ] Sidebar shows: Dashboard, Users, Groups, Audit Logs, Settings
- [ ] Dashboard shows **Your Groups: personal_admin** badge
- [ ] Dashboard shows **Users** card link

---

## S-02 — Admin user-list full permissions

- [ ] Log in as `admin`, navigate to `/users`
- [ ] **Add User** button visible (top-right)
- [ ] **Show Deleted** checkbox visible in filter bar
- [ ] Table column header reads **Groups** (not "Roles")
- [ ] Per-row **Edit** and **Delete** buttons visible
- [ ] Check **Show Deleted** → deleted users appear with strikethrough + **Restore** and **Purge** buttons
- [ ] Select 2+ users via checkboxes → bulk bar shows **Enable**, **Disable**, **Delete**

---

## S-03 — Register new user, verify zero permissions

- [ ] Navigate to `http://localhost:4200/register`
- [ ] Register: `username=testuser`, `email=test@example.com`, `password=Test1234!`
- [ ] Auto-logged in, redirected to `/dashboard`
- [ ] DevTools login response → `user.effectivePermissions` is `[]`
- [ ] Sidebar shows **only** Dashboard and Settings (no Users, Groups, Audit Logs)
- [ ] Navigate manually to `http://localhost:4200/users`
- [ ] No **Add User** button, no **Edit**/**Delete** per row

> **Why no access at all:** the `user-self-read` policy requires `role IN USER,MODERATOR,ADMIN`.
> A freshly registered user has no `role` attribute yet → condition fails → `USER:READ` not granted.

---

## S-04 — Admin grants USER role attribute

- [ ] Open `http://localhost:8080/swagger-ui.html`
- [ ] Authorize with admin Bearer token (from S-01 login response)
- [ ] `GET /api/users?search=testuser` → note the `id` (e.g. `2`)
- [ ] `PUT /api/users/2/attributes`
  ```json
  { "attributeKey": "role", "attributeValue": "USER" }
  ```
- [ ] Response: `200 OK` with `{ "attributeKey": "role", "attributeValue": "USER" }`
- [ ] Log out `testuser`, log back in as `testuser`
- [ ] Login response → `effectivePermissions` contains `["USER:READ"]`
- [ ] Sidebar: **Users** link now visible
- [ ] Navigate `/users` → list loads, **no Add User**, **no Edit/Delete** buttons

---

## S-05 — Admin grants MODERATOR role attribute

- [ ] In Swagger as admin: `PUT /api/users/2/attributes`
  ```json
  { "attributeKey": "role", "attributeValue": "MODERATOR" }
  ```
- [ ] Log out and back in as `testuser`
- [ ] Login response → `effectivePermissions` = `["USER:READ","USER:UPDATE","GROUP:READ"]`
- [ ] `/users` → **Edit** button visible per row, **no Add User**, **no Delete**, **no Show Deleted**
- [ ] `/groups` → list loads, **no Create/Edit/Delete** buttons

---

## S-06 — API enforcement — unauthenticated calls

- [ ] New tab → `http://localhost:8080/api/users` → `401 Unauthorized` (JSON, not redirect)
- [ ] Swagger, no auth → `GET /api/policies` → `401 Unauthorized`
- [ ] Swagger, no auth → `POST /api/users` → `401 Unauthorized`

---

## S-07 — API enforcement — authenticated but no permission

- [ ] Log in as `testuser` (with `role=USER` — only `USER:READ`)
- [ ] Copy `accessToken` from DevTools
- [ ] Swagger → authorize with `testuser`'s token
- [ ] `POST /api/users` with valid body → `403 Forbidden`
- [ ] `DELETE /api/users/1` → `403 Forbidden`
- [ ] `GET /api/policies` → `403 Forbidden`
- [ ] `PUT /api/users/1/attributes` → `403 Forbidden`

---

## S-08 — DENY policy blocks everyone (including admin)

- [ ] Swagger as admin → `POST /api/policies`
  ```json
  {
    "name": "test-deny-user-delete",
    "description": "Block everyone from deleting",
    "effect": "DENY",
    "resource": "USER",
    "action": "DELETE",
    "enabled": true,
    "conditions": []
  }
  ```
- [ ] `201 Created`
- [ ] Log out and back in as `admin`
- [ ] Login response → `USER:DELETE` is **absent** from `effectivePermissions`
- [ ] Navigate `/users` → **Delete** button is gone even for admin
- [ ] **Cleanup:** `DELETE /api/policies/{id}` for the test policy → re-login as admin → `USER:DELETE` is back

---

## S-09 — User attribute CRUD

- [ ] Swagger as admin → `GET /api/users/2/attributes` → returns `[{ "attributeKey": "role", "attributeValue": "MODERATOR" }]`
- [ ] `PUT /api/users/2/attributes`
  ```json
  { "attributeKey": "department", "attributeValue": "engineering" }
  ```
  → `200 OK`, new attribute created
- [ ] `GET /api/users/2/attributes` → returns two attributes: `role` and `department`
- [ ] `DELETE /api/users/2/attributes/department` → `204 No Content`
- [ ] `GET /api/users/2/attributes` → only `role` attribute remains

---

## S-10 — Group form has no role checkboxes

- [ ] Log in as admin → navigate to `/groups`
- [ ] Table has **no "Roles" column** (removed in ABAC migration)
- [ ] Click **Create Group** → form shows **only Name and Description** (no role checkboxes)
- [ ] Fill Name=`Engineering`, Description=`Eng team` → submit
- [ ] New group appears in list
- [ ] Click **Edit** on Engineering → form shows name/description only, no roles
- [ ] Click **Users** → assign `testuser` to the group

---

## S-11 — Token refresh preserves permissions

- [ ] Log in as `admin`
- [ ] In DevTools or Swagger: `POST /api/auth/refresh` with refresh token in `Authorization: Bearer` header
- [ ] Response includes new `accessToken` and `user.effectivePermissions` with all 13 permissions
- [ ] Navigate around the app — all admin buttons still visible without a full re-login

---

## Quick Checklist

| # | Scenario | Status |
|---|---|---|
| S-01 | Admin login — full `effectivePermissions` + full sidebar | ⬜ |
| S-02 | Admin user-list — all action buttons visible | ⬜ |
| S-03 | New user — `[]` permissions, sidebar shows only Dashboard/Settings | ⬜ |
| S-04 | Set `role=USER` → `USER:READ` granted, Users link appears | ⬜ |
| S-05 | Set `role=MODERATOR` → Edit visible, no Delete/Create | ⬜ |
| S-06 | `401` on unauthenticated API calls | ⬜ |
| S-07 | `403` on authenticated calls without matching policy | ⬜ |
| S-08 | No-condition DENY policy blocks even admin | ⬜ |
| S-09 | User attribute CRUD via Swagger | ⬜ |
| S-10 | Group form has no role checkboxes | ⬜ |
| S-11 | Token refresh re-computes `effectivePermissions` | ⬜ |
