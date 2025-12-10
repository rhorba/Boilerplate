import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../core/services/authentication.service';
import { RegisterRequest } from '../../core/models/auth.models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  firstname = '';
  lastname = '';
  email = '';
  password = '';
  message = '';

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) { }

  register() {
    const request: RegisterRequest = {
      firstname: this.firstname,
      lastname: this.lastname,
      email: this.email,
      password: this.password
    };

    this.authService.register(request).subscribe({
      next: (res) => {
        // Auto-login or redirect to login
        // For now, let's treat it as auto-login since service might not store token on register automatically unless we enhanced it
        if (res.access_token) {
          localStorage.setItem('access_token', res.access_token);
          localStorage.setItem('refresh_token', res.refresh_token);
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        this.message = 'Registration failed. Try again.';
        console.error(err);
      }
    });
  }
}
