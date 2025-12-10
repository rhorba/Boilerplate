import { Component, OnInit } from '@angular/core';
import { Page, PageService } from '../../core/services/page.service';
import { Role, RoleService } from '../../core/services/role.service';
import { Action, ActionService } from '../../core/services/action.service';
import { User, UserService } from '../../core/services/user.service';
import { ActivityLog, ActivityLogService } from '../../core/services/activity-log.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {

  activeTab: 'pages' | 'roles' | 'actions' | 'users' | 'logs' = 'pages';

  // Pages
  pages: Page[] = [];
  currentPage: Page = {
    title: '',
    slug: '',
    content: '',
    icon: 'article',
    roles: 'USER,ADMIN'
  };
  isEditing = false;

  // Schema Builder
  currentSchema: { name: string, type: string, label: string, options?: string, uiType?: string }[] = [];
  fieldTypes = ['text', 'number', 'date', 'boolean', 'list'];

  // Access Control
  availableRoles: string[] = [];
  availableActions: string[] = [];
  currentPermissions: { [key: string]: string[] | undefined } = {};

  // Icons
  availableIcons: string[] = [
    'article', 'dashboard', 'settings', 'person', 'people', 'security',
    'lock', 'visibility', 'edit', 'delete', 'add', 'save', 'home',
    'info', 'help', 'check_circle', 'warning', 'error', 'search',
    'menu', 'list', 'grid_view', 'table_view', 'analytics', 'assessment'
  ];

  // Roles Management
  rolesList: Role[] = [];
  newRoleName = '';

  // Actions Management
  actionsList: Action[] = [];
  newActionName = '';

  // User Management
  usersList: User[] = [];

  // Activity Logs
  logsList: ActivityLog[] = [];

  constructor(
    private pageService: PageService,
    private roleService: RoleService,
    private actionService: ActionService,
    private userService: UserService,
    private logService: ActivityLogService
  ) { }

  ngOnInit(): void {
    this.loadPages();
    this.loadRoles();
    this.loadActions();
    this.loadUsers();
    this.loadLogs();
  }

  loadPages() {
    this.pageService.getAllPages().subscribe({
      next: (data) => this.pages = data,
      error: (err) => console.error('Failed to load pages', err)
    });
  }

  loadRoles() {
    this.roleService.getAllRoles().subscribe({
      next: (data) => {
        this.rolesList = data;
        this.availableRoles = data.map(r => r.name);
      },
      error: (err) => console.error('Failed to load roles', err)
    });
  }

  loadActions() {
    this.actionService.getAllActions().subscribe({
      next: (data) => {
        this.actionsList = data;
        this.availableActions = data.map(a => a.name);
      },
      error: (err) => console.error('Failed to load actions', err)
    });
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.usersList = data,
      error: (err) => console.error('Failed to load users', err)
    });
  }

  loadLogs() {
    this.logService.getAllLogs().subscribe({
      next: (data) => this.logsList = data,
      error: (err) => console.error('Failed to load logs', err)
    });
  }

  // --- Page Management ---

  savePage() {
    this.currentPage.schema = JSON.stringify(this.currentSchema);
    this.currentPage.accessControl = JSON.stringify(this.currentPermissions);

    if (this.isEditing && this.currentPage.id) {
      this.pageService.updatePage(this.currentPage.id, this.currentPage).subscribe({
        next: () => {
          this.loadPages();
          this.resetForm();
          alert('Page updated successfully');
        },
        error: (err) => {
          console.error('Update failed', err);
          alert('Failed to update page: ' + (err.error?.message || err.message));
        }
      });
    } else {
      this.pageService.createPage(this.currentPage).subscribe({
        next: () => {
          this.loadPages();
          this.resetForm();
          alert('Page created successfully');
        },
        error: (err) => {
          console.error('Create failed', err);
          alert('Failed to create page: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  editPage(page: Page) {
    this.currentPage = { ...page };
    this.currentSchema = page.schema ? JSON.parse(page.schema) : [];
    this.currentPermissions = page.accessControl ? JSON.parse(page.accessControl) : {};
    this.isEditing = true;
    this.activeTab = 'pages'; // Switch to pages tab
  }

  deletePage(id: number) {
    if (confirm('Are you sure you want to delete this page?')) {
      this.pageService.deletePage(id).subscribe(() => {
        this.loadPages();
      });
    }
  }

  resetForm() {
    this.currentPage = {
      title: '',
      slug: '',
      content: '',
      icon: 'article',
      roles: 'USER,ADMIN',
      schema: '[]',
      accessControl: '{}'
    };
    this.currentSchema = [];
    this.currentPermissions = {};
    this.isEditing = false;
  }

  addField() {
    this.currentSchema.push({ name: '', type: 'text', label: '' });
  }

  removeField(index: number) {
    this.currentSchema.splice(index, 1);
  }

  trackByIndex(index: number, obj: any): any {
    return index;
  }

  togglePermission(action: string, role: string) {
    const roles = this.currentPermissions[action] || [];
    if (roles.includes(role)) {
      this.currentPermissions[action] = roles.filter(r => r !== role);
    } else {
      this.currentPermissions[action] = [...roles, role];
    }
  }

  togglePageRole(role: string) {
    let currentRoles = this.currentPage.roles ? this.currentPage.roles.split(',') : [];
    if (currentRoles.includes(role)) {
      currentRoles = currentRoles.filter(r => r !== role);
    } else {
      currentRoles.push(role);
    }
    this.currentPage.roles = currentRoles.join(',');
  }

  // --- Role Management ---

  createRole() {
    if (!this.newRoleName.trim()) return;
    this.roleService.createRole({ name: this.newRoleName }).subscribe(() => {
      this.newRoleName = '';
      this.loadRoles();
    });
  }

  deleteRole(id: number) {
    if (confirm('Delete role?')) {
      this.roleService.deleteRole(id).subscribe(() => this.loadRoles());
    }
  }

  // --- Action Management ---

  createAction() {
    if (!this.newActionName.trim()) return;
    this.actionService.createAction({ name: this.newActionName }).subscribe(() => {
      this.newActionName = '';
      this.loadActions();
    });
  }

  deleteAction(id: number) {
    if (confirm('Delete action?')) {
      this.actionService.deleteAction(id).subscribe(() => this.loadActions());
    }
  }

  // --- User Management ---

  updateUserRole(user: User, roleName: string) {
    const role = this.rolesList.find(r => r.name === roleName);
    if (!role) return;

    const updatedUser = { ...user, role: role };
    this.userService.updateUser(user.id!, updatedUser).subscribe({
      next: () => {
        this.loadUsers();
        alert('User role updated');
      },
      error: (err) => alert('Failed to update role')
    });
  }

  deleteUser(id: number) {
    if (confirm('Delete user?')) {
      this.userService.deleteUser(id).subscribe(() => this.loadUsers());
    }
  }
}
