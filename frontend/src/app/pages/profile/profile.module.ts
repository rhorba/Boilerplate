import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ProfileComponent } from './profile.component';
import { RouterModule } from '@angular/router';

@NgModule({
    declarations: [ProfileComponent],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        RouterModule.forChild([{ path: '', component: ProfileComponent }])
    ]
})
export class ProfileModule { }
