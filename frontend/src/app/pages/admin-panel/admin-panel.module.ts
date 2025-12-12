import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminPanelComponent } from './admin-panel.component';
import { RouterModule } from '@angular/router';

@NgModule({
    declarations: [AdminPanelComponent],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild([{ path: '', component: AdminPanelComponent }])
    ]
})
export class AdminPanelModule { }
