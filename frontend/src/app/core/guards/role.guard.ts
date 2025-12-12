import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../../core/services/authentication.service';
import { NotificationService } from '../../core/services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private notificationService: NotificationService
  ) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const expectedRoles = route.data['roles'] as Array<string>;
    const userRole = this.authService.getUserRole();

    if (!userRole) {
      this.router.navigate(['/login']);
      return false;
    }

    if (!expectedRoles || expectedRoles.length === 0) {
      return true;
    }

    if (expectedRoles.includes(userRole)) {
      return true;
    }

    // Role not authorized
    // Role not authorized
    this.notificationService.error('You do not have permission to view this page');
    this.router.navigate(['/dashboard']);
    return false;
  }
}
