import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { GroupService } from '../../../core/services/group.service';
import { GroupRequest } from '../../../core/models/group.model';

@Component({
  selector: 'app-group-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './group-form.component.html',
  styleUrl: './group-form.component.css',
})
export class GroupFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private groupService = inject(GroupService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  groupForm: FormGroup;
  isEditMode = false;
  groupId: number | null = null;
  loading = false;
  error: string | null = null;

  constructor() {
    this.groupForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', Validators.maxLength(255)],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if (params['id']) {
        this.isEditMode = true;
        this.groupId = +params['id'];
        this.loadGroup(this.groupId);
      }
    });
  }

  loadGroup(id: number): void {
    this.groupService.getGroupById(id).subscribe({
      next: (group) => {
        this.groupForm.patchValue({
          name: group.name,
          description: group.description,
        });
      },
      error: (err) => {
        this.error = 'Failed to load group';
        console.error(err);
      },
    });
  }

  onSubmit(): void {
    if (this.groupForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;

    const request: GroupRequest = this.groupForm.value;

    const operation =
      this.isEditMode && this.groupId
        ? this.groupService.updateGroup(this.groupId, request)
        : this.groupService.createGroup(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/groups']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to save group';
        this.loading = false;
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/groups']);
  }
}
