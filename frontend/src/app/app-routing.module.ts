import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { MenuComponent } from './components/menu/menu.component';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';


const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    component: MenuComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', loadChildren: () => import('./pages/dashboard/dashboard.module').then(m => m.DashboardModule), canActivate: [AuthGuard] },
      { path: 'profile', loadChildren: () => import('./pages/profile/profile.module').then(m => m.ProfileModule), canActivate: [AuthGuard] },
      { path: 'pages/:slug', loadChildren: () => import('./pages/dynamic-page/dynamic-page.module').then(m => m.DynamicPageModule) },
      {
        path: 'admin',
        loadChildren: () => import('./pages/admin-panel/admin-panel.module').then(m => m.AdminPanelModule),
        canActivate: [AuthGuard, RoleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'settings',
        loadChildren: () => import('./pages/settings/settings.module').then(m => m.SettingsModule),
        canActivate: [AuthGuard, RoleGuard],
        data: { roles: ['ADMIN'] }
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
