import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
})
export class ProfileComponent {
  authService = inject(AuthService);

  get userInitial(): string {
    const user = this.authService.currentUser();
    return user ? user.username.charAt(0).toUpperCase() : '?';
  }
}
