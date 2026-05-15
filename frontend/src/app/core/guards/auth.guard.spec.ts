import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { authGuard } from './auth.guard';
import { TokenService } from '../services/token.service';

describe('authGuard', () => {
  let tokenService: TokenService;
  let router: jasmine.SpyObj<Router>;
  let mockRoute: ActivatedRouteSnapshot;
  let mockState: RouterStateSnapshot;

  const executeGuard = () =>
    TestBed.runInInjectionContext(() => authGuard(mockRoute, mockState));

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree']);

    TestBed.configureTestingModule({
      providers: [
        TokenService,
        { provide: Router, useValue: router },
      ],
    });

    tokenService = TestBed.inject(TokenService);
    localStorage.clear();

    mockRoute = {} as ActivatedRouteSnapshot;
    mockState = { url: '/dashboard' } as RouterStateSnapshot;

    router.createUrlTree.and.returnValue({ toString: () => '/login' } as unknown as UrlTree);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should allow access when user is authenticated', () => {
    tokenService.setAccessToken('valid-token');

    const result = executeGuard();

    expect(result).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should redirect to login when user is not authenticated', () => {
    const result = executeGuard();

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(
      ['/login'],
      { queryParams: { returnUrl: '/dashboard' } }
    );
  });

  it('should preserve the returnUrl in the redirect query params', () => {
    mockState = { url: '/users/settings' } as RouterStateSnapshot;

    executeGuard();

    expect(router.navigate).toHaveBeenCalledWith(
      ['/login'],
      { queryParams: { returnUrl: '/users/settings' } }
    );
  });

  it('should allow access after token is set', () => {
    // Initially no token
    expect(executeGuard()).toBeFalse();

    // Set token
    tokenService.setAccessToken('new-token');

    // Now should allow
    expect(executeGuard()).toBeTrue();
  });
});
