import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { ActivityLog, ActivityLogService } from '../../core/services/activity-log.service';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {

  users: User[] = [];
  logs: ActivityLog[] = [];
  activeTab: 'users' | 'logs' = 'users';
  currentUser: User | null = null;
  isEditing = false;
  showPassword = false;

  constructor(
    private userService: UserService,
    private activityLogService: ActivityLogService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  switchTab(tab: 'users' | 'logs') {
    this.activeTab = tab;
    if (tab === 'users') {
      this.loadUsers();
    } else {
      this.loadLogs();
    }
  }

  loadLogs() {
    this.activityLogService.getAllLogs().subscribe({
      next: (data) => this.logs = data,
      error: (err) => console.error('Failed to load logs', err)
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Failed to load users', err)
    });
  }

  editUser(user: User) {
    this.currentUser = { ...user, password: '' };
    this.isEditing = true;
  }

  deleteUser(id: number) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(id).subscribe(() => {
        this.loadUsers();
      });
    }
  }

  saveUser() {
    if (this.currentUser && this.currentUser.id) {
      this.userService.updateUser(this.currentUser.id, this.currentUser).subscribe({
        next: () => {
          this.loadUsers();
          this.cancelEdit();
          alert('User updated successfully');
        },
        error: (err) => {
          console.error('Update failed', err);
          alert('Failed to update user');
        }
      });
    }
  }

  cancelEdit() {
    this.currentUser = null;
    this.isEditing = false;
  }
}
