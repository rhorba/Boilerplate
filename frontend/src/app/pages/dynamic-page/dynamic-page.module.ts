import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DynamicPageComponent } from './dynamic-page.component';
import { RouterModule } from '@angular/router';

@NgModule({
    declarations: [DynamicPageComponent],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild([{ path: '', component: DynamicPageComponent }])
    ]
})
export class DynamicPageModule { }
