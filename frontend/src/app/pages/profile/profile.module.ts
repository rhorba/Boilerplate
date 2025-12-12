import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileComponent } from './profile.component';
import { RouterModule } from '@angular/router';

@NgModule({
    declarations: [ProfileComponent],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild([{ path: '', component: ProfileComponent }])
    ]
})
export class ProfileModule { }
