import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-6">Dashboard</h1>

      @if (authService.currentUser(); as user) {
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
          <h2 class="text-xl font-semibold mb-4">Welcome, {{ user.username }}!</h2>
          <p class="text-gray-600">Email: {{ user.email }}</p>
          <div class="mt-4">
            <h3 class="font-semibold mb-2">Your Roles:</h3>
            <div class="flex gap-2">
              @for (role of user.roles; track role.id) {
                <span class="inline-flex items-center px-3 py-1 text-sm font-medium bg-blue-100 text-blue-800 rounded-full">
                  {{ role.name }}
                </span>
              }
            </div>
          </div>
        </div>
      }

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        @if (authService.hasPermission('USER_READ')) {
          <a routerLink="/users" class="bg-white shadow-md rounded-lg p-6 hover:shadow-lg transition">
            <h3 class="text-lg font-semibold mb-2">Users</h3>
            <p class="text-gray-600">Manage user accounts</p>
          </a>
        }

        <div class="bg-white shadow-md rounded-lg p-6">
          <h3 class="text-lg font-semibold mb-2">Settings</h3>
          <p class="text-gray-600">Configure application</p>
        </div>

        <button
          (click)="authService.logout()"
          class="bg-red-600 text-white shadow-md rounded-lg p-6 hover:bg-red-700 transition text-left"
        >
          <h3 class="text-lg font-semibold mb-2">Logout</h3>
          <p class="text-red-100">Sign out of your account</p>
        </button>
      </div>
    </div>
  `
})
export class DashboardComponent {
  authService = inject(AuthService);
}
