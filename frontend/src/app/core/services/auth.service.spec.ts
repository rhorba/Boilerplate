import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService, AuthResponse, LoginRequest, RegisterRequest } from './auth.service';
import { TokenService } from './token.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let tokenService: TokenService;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockAuthResponse: AuthResponse = {
    accessToken: 'test-access-token',
    refreshToken: 'test-refresh-token',
    tokenType: 'Bearer',
    expiresIn: 900,
    user: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      enabled: true,
      roles: [],
      groups: [],
      deletedAt: null,
      createdAt: null,
      updatedAt: null,
    },
  };

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        TokenService,
        { provide: Router, useValue: routerSpy },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    tokenService = TestBed.inject(TokenService);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have isAuthenticated false initially', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should have currentUser null initially', () => {
    expect(service.currentUser()).toBeNull();
  });

  describe('login', () => {
    it('should POST to auth/login and store tokens on success', () => {
      const credentials: LoginRequest = { username: 'testuser', password: 'password123' };

      service.login(credentials).subscribe((response) => {
        expect(response.accessToken).toBe('test-access-token');
      });

      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(mockAuthResponse);

      expect(tokenService.getAccessToken()).toBe('test-access-token');
      expect(tokenService.getRefreshToken()).toBe('test-refresh-token');
      expect(service.isAuthenticated()).toBeTrue();
      expect(service.currentUser()?.username).toBe('testuser');
    });
  });

  describe('register', () => {
    it('should POST to auth/register and store tokens on success', () => {
      const data: RegisterRequest = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
      };

      service.register(data).subscribe((response) => {
        expect(response.accessToken).toBe('test-access-token');
      });

      const req = httpMock.expectOne('/api/auth/register');
      expect(req.request.method).toBe('POST');
      req.flush(mockAuthResponse);

      expect(service.isAuthenticated()).toBeTrue();
      expect(service.currentUser()?.username).toBe('testuser');
    });
  });

  describe('logout', () => {
    it('should clear tokens, reset signals, and navigate to login', () => {
      tokenService.setAccessToken('some-token');
      service.logout();

      expect(tokenService.getAccessToken()).toBeNull();
      expect(service.isAuthenticated()).toBeFalse();
      expect(service.currentUser()).toBeNull();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('refreshToken', () => {
    it('should POST to auth/refresh and update access token', () => {
      tokenService.setRefreshToken('old-refresh-token');

      service.refreshToken().subscribe();

      const req = httpMock.expectOne('/api/auth/refresh');
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('Authorization')).toBe('Bearer old-refresh-token');
      req.flush(mockAuthResponse);

      expect(tokenService.getAccessToken()).toBe('test-access-token');
      expect(service.currentUser()?.username).toBe('testuser');
    });
  });

  describe('hasRole', () => {
    it('should return false when user has no roles', () => {
      expect(service.hasRole('ADMIN')).toBeFalse();
    });

    it('should return true when user has the specified role', () => {
      service.login({ username: 'admin', password: 'pass' }).subscribe();
      const req = httpMock.expectOne('/api/auth/login');
      req.flush({
        ...mockAuthResponse,
        user: {
          ...mockAuthResponse.user,
          roles: [{ id: 1, name: 'ADMIN', permissions: [] }],
        },
      });

      expect(service.hasRole('ADMIN')).toBeTrue();
    });

    it('should return false when user does not have the specified role', () => {
      service.login({ username: 'user', password: 'pass' }).subscribe();
      const req = httpMock.expectOne('/api/auth/login');
      req.flush({
        ...mockAuthResponse,
        user: { ...mockAuthResponse.user, roles: [{ id: 2, name: 'USER', permissions: [] }] },
      });

      expect(service.hasRole('ADMIN')).toBeFalse();
    });
  });

  describe('hasPermission', () => {
    it('should return false when user has no permissions', () => {
      expect(service.hasPermission('USER_READ')).toBeFalse();
    });

    it('should return true when user role has the permission', () => {
      service.login({ username: 'admin', password: 'pass' }).subscribe();
      const req = httpMock.expectOne('/api/auth/login');
      req.flush({
        ...mockAuthResponse,
        user: {
          ...mockAuthResponse.user,
          roles: [{ id: 1, name: 'ADMIN', permissions: [{ id: 1, name: 'USER_READ' }] }],
        },
      });

      expect(service.hasPermission('USER_READ')).toBeTrue();
    });
  });
});
