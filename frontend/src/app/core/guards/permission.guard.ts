import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { AuthenticationService } from '../../core/services/authentication.service';
import { NotificationService } from '../../core/services/notification.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';
import { UserGroup } from '../models/user-group.model';

@Injectable({
    providedIn: 'root'
})
export class PermissionGuard implements CanActivate {

    private apiUrl = `${environment.apiUrl}/pages`;

    constructor(
        private authService: AuthenticationService,
        private router: Router,
        private notificationService: NotificationService,
        private http: HttpClient
    ) { }

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

        const user = this.authService.getCurrentUser();

        // If Admin, always allow
        if (user && user.role && user.role.name === 'ADMIN') {
            return true;
        }

        // Identify which page we are accessing. 
        // If it's a static route with 'data.groups', check that.
        // If it's a dynamic page (e.g. /pages/:slug), fetch page metadata?

        const requiredGroups = route.data['groups'] as Array<string>; // If static route has groups defined by name

        if (requiredGroups && requiredGroups.length > 0) {
            if (!user || !user.groups) {
                this.handleUnauthorized();
                return false;
            }
            const hasGroup = user.groups.some((ug: UserGroup) => requiredGroups.includes(ug.name));
            if (hasGroup) return true;
            this.handleUnauthorized();
            return false;
        }

        // If dynamic content (slug), we might need to check backend or rely on backend to throw 403.
        // Assuming backend handles dynamic page security, we just let it pass here?
        // Or if checking logic is client side:
        const slug = route.paramMap.get('slug');
        if (slug) {
            // Optimistic: allow, then handle 403 error in component?
            // Or fetch page info here:
            return this.http.get<any>(`${this.apiUrl}/${slug}`).pipe(
                map(page => {
                    // Check if page has groups assigned
                    if (!page.groups || page.groups.length === 0) return true;

                    // Check intersection
                    // user.groups is UserGroup[], page.groups is UserGroup[]
                    if (!user || !user.groups) return false;

                    const userGroupIds: number[] = user.groups.map((g: UserGroup) => g.id!);
                    const pageHasGroup: boolean = page.groups.some((pg: UserGroup) => pg.id && userGroupIds.includes(pg.id));

                    if (pageHasGroup) return true;

                    this.handleUnauthorized();
                    return false;
                }),
                catchError(() => {
                    // If page not found or error, default to block or 404
                    this.router.navigate(['/404']);
                    return of(false);
                })
            );
        }

        // Default allow if no specific rules
        return true;
    }

    private handleUnauthorized() {
        this.notificationService.error('You do not have permission to view this page');
        this.router.navigate(['/dashboard']);
    }
}
