import { TestBed } from '@angular/core/testing';
import { TokenService } from './token.service';

describe('TokenService', () => {
  let service: TokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenService);
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('access token', () => {
    it('should return null when no access token stored', () => {
      expect(service.getAccessToken()).toBeNull();
    });

    it('should store and retrieve access token', () => {
      service.setAccessToken('test-access-token');
      expect(service.getAccessToken()).toBe('test-access-token');
    });

    it('should overwrite existing access token', () => {
      service.setAccessToken('old-token');
      service.setAccessToken('new-token');
      expect(service.getAccessToken()).toBe('new-token');
    });
  });

  describe('refresh token', () => {
    it('should return null when no refresh token stored', () => {
      expect(service.getRefreshToken()).toBeNull();
    });

    it('should store and retrieve refresh token', () => {
      service.setRefreshToken('test-refresh-token');
      expect(service.getRefreshToken()).toBe('test-refresh-token');
    });
  });

  describe('clearTokens', () => {
    it('should remove both tokens from storage', () => {
      service.setAccessToken('access');
      service.setRefreshToken('refresh');

      service.clearTokens();

      expect(service.getAccessToken()).toBeNull();
      expect(service.getRefreshToken()).toBeNull();
    });

    it('should not throw when called with no tokens stored', () => {
      expect(() => service.clearTokens()).not.toThrow();
    });
  });

  describe('isAuthenticated', () => {
    it('should return false when no access token exists', () => {
      expect(service.isAuthenticated()).toBeFalse();
    });

    it('should return true when access token exists', () => {
      service.setAccessToken('some-token');
      expect(service.isAuthenticated()).toBeTrue();
    });

    it('should return false after tokens are cleared', () => {
      service.setAccessToken('some-token');
      service.clearTokens();
      expect(service.isAuthenticated()).toBeFalse();
    });
  });
});
