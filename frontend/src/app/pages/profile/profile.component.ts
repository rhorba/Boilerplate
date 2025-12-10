import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../core/services/authentication.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  email: string | null = '';
  role: string | null = '';

  constructor(private authService: AuthenticationService) { }

  ngOnInit(): void {
    this.email = this.authService.getUserEmail();
    this.role = this.authService.getUserRole();
  }
}
