import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SettingsComponent } from './settings.component';
import { RouterModule } from '@angular/router';

import { UserGroupListComponent } from './user-groups/user-group-list/user-group-list.component';
import { UserGroupEditComponent } from './user-groups/user-group-edit/user-group-edit.component';

@NgModule({
    declarations: [SettingsComponent, UserGroupListComponent, UserGroupEditComponent],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild([
            { path: '', component: SettingsComponent },
            { path: 'user-groups', component: UserGroupListComponent },
            { path: 'user-groups/new', component: UserGroupEditComponent },
            { path: 'user-groups/edit/:id', component: UserGroupEditComponent }
        ])
    ]
})
export class SettingsModule { }
