import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from '../../core/services/profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService
  ) { }

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      email: [{ value: '', disabled: true }],
      role: [{ value: '', disabled: true }],
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      password: [''] // Optional
    });

    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getMyProfile().subscribe({
      next: (user) => {
        this.profileForm.patchValue({
          email: user.email,
          role: user.role?.name || 'USER', // Handle nested role object or string
          firstname: user.firstname,
          lastname: user.lastname
        });
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load profile';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const payload = {
      ...this.profileForm.getRawValue(),
      password: this.profileForm.value.password || undefined // Only send if not empty
    };

    if (!payload.password) delete payload.password;

    this.profileService.updateMyProfile(payload).subscribe({
      next: (user) => {
        this.successMessage = 'Profile updated successfully';
        this.loadProfile(); // Reload to refresh data
      },
      error: (err) => {
        this.errorMessage = 'Failed to update profile';
        this.loading = false;
      }
    });
  }
}
