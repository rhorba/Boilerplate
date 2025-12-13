import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs'; // For mocking Observables

import { PermissionGuard } from './permission.guard';
import { AuthenticationService } from '../../core/services/authentication.service';
import { NotificationService } from '../../core/services/notification.service';
import { User } from '../models/user.model';

describe('PermissionGuard', () => {
    let guard: PermissionGuard;
    let authServiceSpy: jasmine.SpyObj<AuthenticationService>;
    let routerSpy: jasmine.SpyObj<Router>;
    let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

    beforeEach(() => {
        const authSpy = jasmine.createSpyObj('AuthenticationService', ['getCurrentUser']);
        const notifSpy = jasmine.createSpyObj('NotificationService', ['error']);
        const routerMock = jasmine.createSpyObj('Router', ['navigate']);

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, RouterTestingModule],
            providers: [
                PermissionGuard,
                { provide: AuthenticationService, useValue: authSpy },
                { provide: NotificationService, useValue: notifSpy },
                { provide: Router, useValue: routerMock }
            ]
        });

        guard = TestBed.inject(PermissionGuard);
        authServiceSpy = TestBed.inject(AuthenticationService) as jasmine.SpyObj<AuthenticationService>;
        notificationServiceSpy = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
        routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    });

    it('should be created', () => {
        expect(guard).toBeTruthy();
    });

    it('should allow access for ADMIN', () => {
        const adminUser: User = { id: 1, email: 'admin@test.com', role: { id: 1, name: 'ADMIN' }, groups: [] };
        authServiceSpy.getCurrentUser.and.returnValue(adminUser);

        const route = { data: { groups: ['SomeGroup'] } } as any as ActivatedRouteSnapshot;
        const result = guard.canActivate(route, null!);

        expect(result).toBeTrue();
    });

    it('should block access if user not logged in', () => {
        authServiceSpy.getCurrentUser.and.returnValue(null!);
        const route = { data: { groups: ['SomeGroup'] } } as any as ActivatedRouteSnapshot;

        guard.canActivate(route, null!);

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
        expect(notificationServiceSpy.error).toHaveBeenCalled();
    });

    it('should block access if user does not have required group', () => {
        const user: User = { id: 2, email: 'user@test.com', role: { id: 2, name: 'USER' }, groups: [{ id: 1, name: 'OtherGroup', description: '' }] };
        authServiceSpy.getCurrentUser.and.returnValue(user);
        const route = { data: { groups: ['RequiredGroup'] } } as any as ActivatedRouteSnapshot;

        guard.canActivate(route, null!);

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
    });

    it('should allow access if user has required group', () => {
        const user: User = { id: 2, email: 'user@test.com', role: { id: 2, name: 'USER' }, groups: [{ id: 1, name: 'RequiredGroup', description: '' }] };
        authServiceSpy.getCurrentUser.and.returnValue(user);
        const route = { data: { groups: ['RequiredGroup'] } } as any as ActivatedRouteSnapshot;

        const result = guard.canActivate(route, null!);

        expect(result).toBeTrue();
    });
});
