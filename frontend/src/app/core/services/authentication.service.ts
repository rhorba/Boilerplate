import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationRequest, AuthenticationResponse, RegisterRequest } from '../models/auth.models';
import { tap } from 'rxjs/operators';
import { jwtDecode } from "jwt-decode";
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    private baseUrl = `${environment.apiUrl}/auth`;

    constructor(private http: HttpClient) { }

    register(request: RegisterRequest): Observable<AuthenticationResponse> {
        return this.http.post<AuthenticationResponse>(`${this.baseUrl}/register`, request)
            .pipe(
                tap(response => {
                    if (response.access_token) {
                        this.setSession(response);
                    }
                })
            );
    }

    authenticate(request: AuthenticationRequest): Observable<AuthenticationResponse> {
        return this.http.post<AuthenticationResponse>(`${this.baseUrl}/authenticate`, request)
            .pipe(
                tap(response => this.setSession(response))
            );
    }

    logout() {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('access_token');
    }

    private setSession(response: AuthenticationResponse) {
        localStorage.setItem('access_token', response.access_token);
        localStorage.setItem('refresh_token', response.refresh_token);
    }

    getUserRole(): string | null {
        const token = localStorage.getItem('access_token');
        if (!token) return null;
        try {
            const decoded: any = jwtDecode(token);
            // Spring Security authorities are often in "authorities" array or "role" claim
            // Adjust based on your JWT payload.
            // Assuming boilerplate puts role in 'role' or authority in 'sub' is just email
            // Let's assume standard 'role' claim or check how we built it in backend
            return decoded.role || (decoded.authorities && decoded.authorities[0]) || 'USER';
        } catch (e) {
            return null;
        }
    }

    getUserEmail(): string | null {
        const token = localStorage.getItem('access_token');
        if (!token) return null;
        try {
            const decoded: any = jwtDecode(token);
            return decoded.sub; // Subject is email
        } catch (e) {
            return null;
        }
    }
}
