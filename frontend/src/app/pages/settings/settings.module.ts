import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SettingsComponent } from './settings.component';
import { RouterModule } from '@angular/router';

@NgModule({
    declarations: [SettingsComponent],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild([{ path: '', component: SettingsComponent }])
    ]
})
export class SettingsModule { }
