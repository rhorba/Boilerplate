import { TestBed } from '@angular/core/testing';
import {
  HttpClient,
  HttpErrorResponse,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { errorInterceptor } from './error.interceptor';
import { AuthService } from '../services/auth.service';
import { TokenService } from '../services/token.service';

describe('errorInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authService: AuthService;
  let logoutSpy: jasmine.Spy;

  beforeEach(() => {
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        TokenService,
        AuthService,
        { provide: Router, useValue: routerSpy },
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
    logoutSpy = spyOn(authService, 'logout');
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should call authService.logout() on 401 response', () => {
    httpClient.get('/api/users').subscribe({
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(401);
      },
    });

    const req = httpMock.expectOne('/api/users');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(logoutSpy).toHaveBeenCalledTimes(1);
  });

  it('should NOT call logout on 403 response', () => {
    httpClient.get('/api/admin').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/admin');
    req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

    expect(logoutSpy).not.toHaveBeenCalled();
  });

  it('should NOT call logout on 404 response', () => {
    httpClient.get('/api/users/99999').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/users/99999');
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(logoutSpy).not.toHaveBeenCalled();
  });

  it('should NOT call logout on 500 response', () => {
    httpClient.get('/api/users').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/users');
    req.flush('Internal Server Error', { status: 500, statusText: 'Server Error' });

    expect(logoutSpy).not.toHaveBeenCalled();
  });

  it('should re-throw the error after handling', (done) => {
    httpClient.get('/api/users').subscribe({
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(401);
        done();
      },
    });

    const req = httpMock.expectOne('/api/users');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
  });
});
