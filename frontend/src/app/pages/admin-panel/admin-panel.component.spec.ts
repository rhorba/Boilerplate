import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminPanelComponent } from './admin-panel.component';
import { UserService } from '../../core/services/user.service';
import { ActivityLogService } from '../../core/services/activity-log.service';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { User } from '../../core/models/user.model';

describe('AdminPanelComponent', () => {
    let component: AdminPanelComponent;
    let fixture: ComponentFixture<AdminPanelComponent>;
    let userService: jasmine.SpyObj<UserService>;
    let activityLogService: jasmine.SpyObj<ActivityLogService>;

    const mockUsers: User[] = [
        { id: 1, email: 'admin@test.com', firstname: 'Admin', lastname: 'User', role: { id: 1, name: 'ADMIN' } },
        { id: 2, email: 'user@test.com', firstname: 'Test', lastname: 'User', role: { id: 2, name: 'USER' } }
    ];

    const mockLogs = [
        { id: 1, action: 'LOGIN', userEmail: 'admin@test.com', timestamp: '2023-01-01', description: 'User login' }
    ];

    beforeEach(() => {
        const userServiceSpy = jasmine.createSpyObj('UserService', ['getAllUsers', 'updateUser', 'deleteUser']);
        const activityLogServiceSpy = jasmine.createSpyObj('ActivityLogService', ['getAllLogs']);

        TestBed.configureTestingModule({
            declarations: [AdminPanelComponent],
            imports: [FormsModule, HttpClientTestingModule],
            providers: [
                { provide: UserService, useValue: userServiceSpy },
                { provide: ActivityLogService, useValue: activityLogServiceSpy }
            ]
        });

        fixture = TestBed.createComponent(AdminPanelComponent);
        component = fixture.componentInstance;
        userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
        activityLogService = TestBed.inject(ActivityLogService) as jasmine.SpyObj<ActivityLogService>;

        userService.getAllUsers.and.returnValue(of(mockUsers));
        activityLogService.getAllLogs.and.returnValue(of(mockLogs));
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load users on init', () => {
        fixture.detectChanges(); // triggers ngOnInit
        expect(userService.getAllUsers).toHaveBeenCalled();
        expect(component.users).toEqual(mockUsers);
    });

    it('should switch tabs', () => {
        component.switchTab('logs');
        expect(component.activeTab).toBe('logs');
        expect(activityLogService.getAllLogs).toHaveBeenCalled();

        component.switchTab('users');
        expect(component.activeTab).toBe('users');
        expect(userService.getAllUsers).toHaveBeenCalledTimes(1); // Called once in ngOnInit (beforeEach isn't resetting calls count implicitly unless configured, but here it's fine)
    });

    it('should prepare user for editing', () => {
        const userToEdit = mockUsers[1];
        component.editUser(userToEdit);

        expect(component.isEditing).toBeTrue();
        expect(component.currentUser).toEqual({ ...userToEdit, password: '' });
    });

    it('should cancel edit', () => {
        component.isEditing = true;
        component.currentUser = mockUsers[0];
        component.cancelEdit();

        expect(component.isEditing).toBeFalse();
        expect(component.currentUser).toBeNull();
    });

    it('should update user successfully', () => {
        spyOn(window, 'alert');
        const updatedUser = { ...mockUsers[1], firstname: 'Updated' };
        userService.updateUser.and.returnValue(of(updatedUser));

        component.currentUser = updatedUser;
        component.saveUser();

        expect(userService.updateUser).toHaveBeenCalledWith(updatedUser.id!, updatedUser);
        expect(userService.getAllUsers).toHaveBeenCalled(); // Should reload
        expect(component.isEditing).toBeFalse();
        expect(window.alert).toHaveBeenCalledWith('User updated successfully');
    });

    it('should handle update error', () => {
        spyOn(window, 'alert');
        const user = { ...mockUsers[1] };
        userService.updateUser.and.returnValue(throwError(() => new Error('Update failed')));

        component.currentUser = user;
        component.saveUser();

        expect(userService.updateUser).toHaveBeenCalled();
        expect(window.alert).toHaveBeenCalledWith('Failed to update user');
    });

    it('should delete user when confirmed', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        userService.deleteUser.and.returnValue(of(void 0));

        component.deleteUser(2);

        expect(userService.deleteUser).toHaveBeenCalledWith(2);
        expect(userService.getAllUsers).toHaveBeenCalled();
    });

    it('should not delete user when cancelled', () => {
        spyOn(window, 'confirm').and.returnValue(false);

        component.deleteUser(2);

        expect(userService.deleteUser).not.toHaveBeenCalled();
    });

    it('should toggle password visibility', () => {
        expect(component.showPassword).toBeFalse();
        component.togglePassword();
        expect(component.showPassword).toBeTrue();
        component.togglePassword();
        expect(component.showPassword).toBeFalse();
    });
});
