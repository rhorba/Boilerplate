import { Component, OnInit } from '@angular/core';
import { UserGroup } from '../../../../core/models/user-group.model';
import { UserGroupService } from '../../../../core/services/user-group.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'app-user-group-edit',
    templateUrl: './user-group-edit.component.html',
    styleUrls: ['./user-group-edit.component.scss']
})
export class UserGroupEditComponent implements OnInit {

    group: UserGroup = {
        id: 0,
        name: '',
        description: ''
    };
    isEditMode = false;

    constructor(
        private userGroupService: UserGroupService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.isEditMode = true;
            this.userGroupService.getGroupById(+id).subscribe(g => this.group = g);
        }
    }

    saveGroup() {
        if (this.isEditMode) {
            this.userGroupService.updateGroup(this.group.id, this.group).subscribe(() => {
                this.router.navigate(['/settings/user-groups']);
            });
        } else {
            this.userGroupService.createGroup(this.group).subscribe(() => {
                this.router.navigate(['/settings/user-groups']);
            });
        }
    }

    cancel() {
        this.router.navigate(['/settings/user-groups']);
    }
}
