import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserGroup } from '../models/user-group.model';

@Injectable({
    providedIn: 'root'
})
export class UserGroupService {
    private apiUrl = `${environment.apiUrl}/user-groups`;

    constructor(private http: HttpClient) { }

    getAllGroups(): Observable<UserGroup[]> {
        return this.http.get<UserGroup[]>(this.apiUrl);
    }

    getGroupById(id: number): Observable<UserGroup> {
        return this.http.get<UserGroup>(`${this.apiUrl}/${id}`);
    }

    createGroup(group: UserGroup): Observable<UserGroup> {
        return this.http.post<UserGroup>(this.apiUrl, group);
    }

    updateGroup(id: number, group: UserGroup): Observable<UserGroup> {
        return this.http.put<UserGroup>(`${this.apiUrl}/${id}`, group);
    }

    deleteGroup(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
