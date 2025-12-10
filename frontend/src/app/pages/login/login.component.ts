import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../core/services/authentication.service';
import { AuthenticationRequest } from '../../core/models/auth.models';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  message = '';

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) { }

  login() {
    const request: AuthenticationRequest = {
      email: this.email,
      password: this.password
    };

    this.authService.authenticate(request).subscribe({
      next: (res) => {
        // Token is already stored by service tap()
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.message = 'Login failed. Please check your credentials.';
        console.error(err);
      }
    });
  }
}
