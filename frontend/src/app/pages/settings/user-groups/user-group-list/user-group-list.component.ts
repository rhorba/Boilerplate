import { Component, OnInit } from '@angular/core';
import { UserGroup } from '../../../../core/models/user-group.model';
import { UserGroupService } from '../../../../core/services/user-group.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-user-group-list',
    templateUrl: './user-group-list.component.html',
    styleUrls: ['./user-group-list.component.scss']
})
export class UserGroupListComponent implements OnInit {

    groups: UserGroup[] = [];

    constructor(private userGroupService: UserGroupService, private router: Router) { }

    ngOnInit(): void {
        this.loadGroups();
    }

    loadGroups() {
        this.userGroupService.getAllGroups().subscribe(data => {
            this.groups = data;
        });
    }

    createGroup() {
        this.router.navigate(['/settings/user-groups/new']);
    }

    editGroup(group: UserGroup) {
        if (group.id) {
            this.router.navigate(['/settings/user-groups/edit', group.id]);
        }
    }

    // Inline Editing
    editingCell: { id: number, field: string } | null = null;

    startEdit(id: number, field: string) {
        this.editingCell = { id, field };
    }

    stopEdit(group: UserGroup) {
        if (this.editingCell && this.editingCell.id === group.id) {
            this.editingCell = null;
            if (group.id) {
                this.userGroupService.updateGroup(group.id, group).subscribe(
                    () => {
                        // Success toast or feedback could go here
                    },
                    (err) => {
                        console.error('Failed to update group', err);
                        this.loadGroups(); // Revert on error
                    }
                );
            }
        }
    }

    isEditing(id: number | undefined, field: string): boolean {
        return !!id && !!this.editingCell && this.editingCell.id === id && this.editingCell.field === field;
    }

    deleteGroup(id?: number) {
        if (id && confirm('Are you sure you want to delete this group?')) {
            this.userGroupService.deleteGroup(id).subscribe(() => {
                this.loadGroups();
            });
        }
    }
}
