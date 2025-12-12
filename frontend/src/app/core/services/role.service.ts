import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Role {
    id?: number;
    name: string;
}

@Injectable({
    providedIn: 'root'
})
export class RoleService {
    private apiUrl = `${environment.apiUrl}/roles`;

    constructor(private http: HttpClient) { }

    getAllRoles(): Observable<Role[]> {
        return this.http.get<Role[]>(this.apiUrl);
    }

    createRole(role: Role): Observable<Role> {
        return this.http.post<Role>(this.apiUrl, role);
    }

    deleteRole(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
