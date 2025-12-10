import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ActivityLog {
    id: number;
    action: string;
    description: string;
    userEmail: string;
    timestamp: string;
}

@Injectable({
    providedIn: 'root'
})
export class ActivityLogService {

    private apiUrl = 'http://localhost:8080/api/v1/activity-logs';

    constructor(private http: HttpClient) { }

    getAllLogs(): Observable<ActivityLog[]> {
        return this.http.get<ActivityLog[]>(this.apiUrl);
    }
}
