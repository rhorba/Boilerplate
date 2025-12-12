import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Action {
    id?: number;
    name: string;
}

@Injectable({
    providedIn: 'root'
})
export class ActionService {
    private apiUrl = `${environment.apiUrl}/actions`;

    constructor(private http: HttpClient) { }

    getAllActions(): Observable<Action[]> {
        return this.http.get<Action[]>(this.apiUrl);
    }

    createAction(action: Action): Observable<Action> {
        return this.http.post<Action>(this.apiUrl, action);
    }

    deleteAction(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
