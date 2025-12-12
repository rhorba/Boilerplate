import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ActivityLog {
    id?: number;
    action: string;
    description: string;
    userEmail: string;
    timestamp: string;
}

@Injectable({
    providedIn: 'root'
})
export class ActivityLogService {
    private apiUrl = `${environment.apiUrl}/activity-logs`;

    constructor(private http: HttpClient) { }

    getAllLogs(): Observable<ActivityLog[]> {
        return this.http.get<ActivityLog[]>(this.apiUrl);
    }

    clearLogs(): Observable<void> {
        return this.http.delete<void>(this.apiUrl);
    }
}
