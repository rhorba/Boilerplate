import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { TokenService } from '../services/token.service';

describe('authInterceptor', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let tokenService: TokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TokenService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    tokenService = TestBed.inject(TokenService);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should add Authorization header when token exists', () => {
    tokenService.setAccessToken('test-token');

    httpClient.get('/api/users').subscribe();

    const req = httpMock.expectOne('/api/users');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([]);
  });

  it('should NOT add Authorization header when no token', () => {
    httpClient.get('/api/users').subscribe();

    const req = httpMock.expectOne('/api/users');
    expect(req.request.headers.get('Authorization')).toBeNull();
    req.flush([]);
  });

  it('should NOT add Authorization header for login endpoint even if token exists', () => {
    tokenService.setAccessToken('test-token');

    httpClient.post('/api/auth/login', {}).subscribe();

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.headers.get('Authorization')).toBeNull();
    req.flush({});
  });

  it('should add Authorization header for non-login endpoints', () => {
    tokenService.setAccessToken('my-token');

    httpClient.get('/api/profile/me').subscribe();

    const req = httpMock.expectOne('/api/profile/me');
    expect(req.request.headers.get('Authorization')).toBe('Bearer my-token');
    req.flush({});
  });

  it('should not modify the original request URL', () => {
    tokenService.setAccessToken('test-token');

    httpClient.get('/api/groups').subscribe();

    const req = httpMock.expectOne('/api/groups');
    expect(req.request.url).toBe('/api/groups');
    req.flush([]);
  });
});
