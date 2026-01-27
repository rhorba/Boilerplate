import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  inject,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { UserResponse, RoleResponse, UpdateUserRequest } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-edit-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-edit-panel.component.html',
})
export class UserEditPanelComponent implements OnInit {
  @Input({ required: true }) user!: UserResponse;
  @Input() roles: RoleResponse[] = [];
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  loading = signal(false);
  error = signal<string | null>(null);
  showPassword = signal(false);

  editForm = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
    enabled: [true],
    roleIds: [[] as number[]],
  });

  ngOnInit(): void {
    this.editForm.patchValue({
      username: this.user.username,
      email: this.user.email,
      enabled: this.user.enabled,
      roleIds: this.user.roles.map((r) => r.id),
    });
  }

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    this.close.emit();
  }

  toggleRole(roleId: number): void {
    const current = this.editForm.get('roleIds')!.value;
    const index = current.indexOf(roleId);
    if (index === -1) {
      this.editForm.get('roleIds')!.setValue([...current, roleId]);
    } else {
      this.editForm.get('roleIds')!.setValue(current.filter((id: number) => id !== roleId));
    }
  }

  isRoleSelected(roleId: number): boolean {
    return this.editForm.get('roleIds')!.value.includes(roleId);
  }

  onSubmit(): void {
    if (this.editForm.invalid) return;

    this.loading.set(true);
    this.error.set(null);

    const formValue = this.editForm.getRawValue();
    const request: UpdateUserRequest = {};

    if (formValue.username !== this.user.username) request.username = formValue.username;
    if (formValue.email !== this.user.email) request.email = formValue.email;
    if (formValue.password) request.password = formValue.password;
    if (formValue.enabled !== this.user.enabled) request.enabled = formValue.enabled;

    const originalRoleIds = this.user.roles.map((r) => r.id).sort();
    const newRoleIds = formValue.roleIds.sort();
    if (JSON.stringify(originalRoleIds) !== JSON.stringify(newRoleIds)) {
      request.roleIds = formValue.roleIds;
    }

    // Only send if there are changes
    if (Object.keys(request).length === 0) {
      this.close.emit();
      return;
    }

    this.userService.updateUser(this.user.id, request).subscribe({
      next: () => {
        this.loading.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Failed to update user');
        this.loading.set(false);
      },
    });
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('panel-overlay')) {
      this.close.emit();
    }
  }
}
