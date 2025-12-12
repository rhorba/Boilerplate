import { Component, OnInit } from '@angular/core';
import { Page, PageService } from '../../core/services/page.service';
import { Role, RoleService } from '../../core/services/role.service';
import { Action, ActionService } from '../../core/services/action.service';
import { User, UserService } from '../../core/services/user.service';
import { ActivityLog, ActivityLogService } from '../../core/services/activity-log.service';
import { NotificationService } from '../../core/services/notification.service';

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
  filteredUsers: User[] = [];
  paginatedUsers: User[] = [];

  // Pagination & Filtering
  userPage = 1;
  userPageSize = 5;
  userTotal = 0;
  userFilter: { firstname: string, lastname: string, email: string, role: string } = { firstname: '', lastname: '', email: '', role: '' };
  isEditingUser = false;
  currentUserEdit: User = { email: '', firstname: '', lastname: '' };


  // Activity Logs
  logsList: ActivityLog[] = [];
  filteredLogs: ActivityLog[] = [];
  paginatedLogs: ActivityLog[] = [];

  // Log Pagination & Filtering
  logPage = 1;
  logPageSize = 5;
  logTotal = 0;
  logFilter: { action: string, email: string } = { action: '', email: '' };

  constructor(
    private pageService: PageService,
    private roleService: RoleService,
    private actionService: ActionService,
    private userService: UserService,
    private logService: ActivityLogService,
    private notificationService: NotificationService
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
      next: (data) => {
        this.usersList = data.sort((a, b) => (a.id || 0) - (b.id || 0)); // Sort by ID stable
        this.applyUserFilter();
      },
      error: (err) => console.error('Failed to load users', err)
    });
  }

  applyUserFilter() {
    let filtered = this.usersList;

    if (this.userFilter.firstname) {
      const term = this.userFilter.firstname.toLowerCase();
      filtered = filtered.filter(u => u.firstname?.toLowerCase().includes(term));
    }

    if (this.userFilter.lastname) {
      const term = this.userFilter.lastname.toLowerCase();
      filtered = filtered.filter(u => u.lastname?.toLowerCase().includes(term));
    }

    if (this.userFilter.email) {
      const term = this.userFilter.email.toLowerCase();
      filtered = filtered.filter(u => u.email.toLowerCase().includes(term));
    }

    if (this.userFilter.role) {
      const term = this.userFilter.role.toLowerCase();
      filtered = filtered.filter(u => u.role?.name.toLowerCase().includes(term));
    }

    this.filteredUsers = filtered;
    this.userTotal = filtered.length;
    this.updateUserPagination();
  }

  updateUserPagination() {
    const startIndex = (this.userPage - 1) * this.userPageSize;
    this.paginatedUsers = this.filteredUsers.slice(startIndex, startIndex + this.userPageSize);
  }

  changeUserPage(page: number) {
    this.userPage = page;
    this.updateUserPagination();
  }

  get userTotalPages(): number {
    return Math.ceil(this.userTotal / this.userPageSize);
  }

  loadLogs() {
    this.logService.getAllLogs().subscribe({
      next: (data) => {
        this.logsList = data.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()); // Sort new to old
        this.applyLogFilter();
      },
      error: (err) => console.error('Failed to load logs', err)
    });
  }

  applyLogFilter() {
    let filtered = this.logsList;

    if (this.logFilter.action) {
      const term = this.logFilter.action.toLowerCase();
      filtered = filtered.filter(l => l.action.toLowerCase().includes(term));
    }

    if (this.logFilter.email) {
      const term = this.logFilter.email.toLowerCase();
      filtered = filtered.filter(l => l.userEmail.toLowerCase().includes(term));
    }

    this.filteredLogs = filtered;
    this.logTotal = filtered.length;
    this.updateLogPagination();
  }

  updateLogPagination() {
    const startIndex = (this.logPage - 1) * this.logPageSize;
    this.paginatedLogs = this.filteredLogs.slice(startIndex, startIndex + this.logPageSize);
  }

  changeLogPage(page: number) {
    this.logPage = page;
    this.updateLogPagination();
  }

  get logTotalPages(): number {
    return Math.ceil(this.logTotal / this.logPageSize);
  }

  clearLogs() {
    if (confirm('Are you sure you want to clear all activity logs? This action cannot be undone.')) {
      this.logService.clearLogs().subscribe({
        next: () => {
          this.notificationService.success('All logs cleared successfully');
          this.loadLogs();
        },
        error: (err) => {
          console.error('Failed to clear logs', err);
          this.notificationService.error('Failed to clear logs');
        }
      });
    }
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
          this.notificationService.success('Page updated successfully');
        },
        error: (err) => {
          console.error('Update failed', err);
          this.notificationService.error('Failed to update page: ' + (err.error?.message || err.message));
        }
      });
    } else {
      this.pageService.createPage(this.currentPage).subscribe({
        next: () => {
          this.loadPages();
          this.resetForm();
          this.notificationService.success('Page created successfully');
        },
        error: (err) => {
          console.error('Create failed', err);
          this.notificationService.error('Failed to create page: ' + (err.error?.message || err.message));
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

  hasPageRole(role: string): boolean {
    if (!this.currentPage.roles) return false;
    return this.currentPage.roles.split(',').includes(role);
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
        // Update local list instead of full reload to prevent visual jumping
        const index = this.usersList.findIndex(u => u.id === user.id);
        if (index !== -1) {
          this.usersList[index] = { ...user, role: role };
          this.applyUserFilter(); // Re-apply filter/sort
        }
        this.notificationService.success('User role updated');
      },
      error: (err) => this.notificationService.error('Failed to update role')
    });
  }

  editUser(user: User) {
    this.isEditingUser = true;
    this.currentUserEdit = { ...user };
    // ensure role is set correctly for comparison
    if (this.currentUserEdit.role) {
      const foundRole = this.rolesList.find(r => r.name === this.currentUserEdit.role?.name);
      if (foundRole) {
        this.currentUserEdit.role = foundRole;
      }
    }
  }

  cancelUserEdit() {
    this.isEditingUser = false;
    this.currentUserEdit = { email: '', firstname: '', lastname: '' };
  }

  saveUser() {
    if (!this.currentUserEdit.id) return;

    this.userService.updateUser(this.currentUserEdit.id, this.currentUserEdit).subscribe({
      next: (updatedUser) => {
        const index = this.usersList.findIndex(u => u.id === updatedUser.id);
        if (index !== -1) {
          this.usersList[index] = updatedUser;
          this.applyUserFilter();
        }
        this.notificationService.success('User updated successfully');
        this.cancelUserEdit();
      },
      error: (err) => {
        console.error('Update user failed', err);
        this.notificationService.error('Failed to update user');
      }
    });
  }

  deleteUser(id: number) {
    if (confirm('Delete user?')) {
      this.userService.deleteUser(id).subscribe(() => this.loadUsers());
    }
  }

  compareRoles(r1: Role, r2: Role): boolean {
    return r1 && r2 ? r1.id === r2.id : r1 === r2;
  }
}
