import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService, PageResponse } from '../../../services/user.service';
import { AuthService, UserResponse } from '../../../core/services/auth.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  authService = inject(AuthService);

  users = signal<PageResponse<UserResponse> | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  page = signal(0);
  size = signal(10);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.error.set(null);

    this.userService.getUsers(this.page(), this.size()).subscribe({
      next: (data) => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load users');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  nextPage(): void {
    const data = this.users();
    if (data && this.page() < data.totalPages - 1) {
      this.page.update(p => p + 1);
      this.loadUsers();
    }
  }

  previousPage(): void {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadUsers();
    }
  }

  deleteUser(id: number): void {
    if (!confirm('Are you sure you want to delete this user?')) return;

    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        alert('Failed to delete user');
        console.error(err);
      }
    });
  }
}
