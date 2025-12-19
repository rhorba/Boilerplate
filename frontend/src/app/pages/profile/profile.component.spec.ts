import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ProfileService } from '../../core/services/profile.service';
import { of, throwError } from 'rxjs';

describe('ProfileComponent', () => {
    let component: ProfileComponent;
    let fixture: ComponentFixture<ProfileComponent>;
    let profileServiceMock: any;

    const mockUser = {
        id: 1,
        email: 'test@example.com',
        role: { name: 'USER' },
        firstname: 'John',
        lastname: 'Doe'
    };

    beforeEach(async () => {
        profileServiceMock = {
            getMyProfile: jasmine.createSpy('getMyProfile').and.returnValue(of(mockUser)),
            updateMyProfile: jasmine.createSpy('updateMyProfile').and.returnValue(of(mockUser))
        };

        await TestBed.configureTestingModule({
            declarations: [ProfileComponent],
            imports: [ReactiveFormsModule],
            providers: [
                { provide: ProfileService, useValue: profileServiceMock }
            ]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProfileComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize form with user data', () => {
        expect(profileServiceMock.getMyProfile).toHaveBeenCalled();
        expect(component.profileForm.get('email')?.value).toBe('test@example.com');
        expect(component.profileForm.get('firstname')?.value).toBe('John');
    });

    it('should disable specific fields', () => {
        expect(component.profileForm.get('email')?.disabled).toBeTrue();
        expect(component.profileForm.get('role')?.disabled).toBeTrue();
    });

    it('should validate required fields', () => {
        component.profileForm.get('firstname')?.setValue('');
        expect(component.profileForm.get('firstname')?.valid).toBeFalse();
    });

    it('should call updateMyProfile on submit', () => {
        component.profileForm.get('firstname')?.setValue('Jane');
        component.onSubmit();

        expect(profileServiceMock.updateMyProfile).toHaveBeenCalledWith(jasmine.objectContaining({
            firstname: 'Jane',
            email: 'test@example.com'
        }));
    });

    it('should handle update errors', () => {
        profileServiceMock.updateMyProfile.and.returnValue(throwError(() => new Error('Error')));
        component.profileForm.get('firstname')?.setValue('Jane');
        component.onSubmit();

        expect(component.errorMessage).toBe('Failed to update profile');
    });
});
