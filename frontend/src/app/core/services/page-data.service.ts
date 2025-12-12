import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PageData {
    id?: number;
    pageId: number;
    data: string; // JSON string
}

@Injectable({
    providedIn: 'root'
})
export class PageDataService {
    private apiUrl = `${environment.apiUrl}/page-data`;

    constructor(private http: HttpClient) { }

    getAllDataByPageId(pageId: number): Observable<PageData[]> {
        return this.http.get<PageData[]>(`${this.apiUrl}/page/${pageId}`);
    }

    getPageDataById(id: number): Observable<PageData> {
        return this.http.get<PageData>(`${this.apiUrl}/${id}`);
    }

    createPageData(pageData: PageData): Observable<PageData> {
        return this.http.post<PageData>(this.apiUrl, pageData);
    }

    updatePageData(id: number, pageData: PageData): Observable<PageData> {
        return this.http.put<PageData>(`${this.apiUrl}/${id}`, pageData);
    }

    deletePageData(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
