import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class PageService {
  private apiUrl = `${environment.apiUrl}/pages`;
  private refreshPages$ = new Subject<void>();

  get refreshPages() {
    return this.refreshPages$;
  }

  constructor(private http: HttpClient) { }

  getAllPages(): Observable<Page[]> {
    return this.http.get<Page[]>(this.apiUrl);
  }

  getPageBySlug(slug: string): Observable<Page> {
    return this.http.get<Page>(`${this.apiUrl}/${slug}`);
  }

  createPage(page: Page): Observable<Page> {
    return this.http.post<Page>(this.apiUrl, page).pipe(
      tap(() => this.refreshPages$.next())
    );
  }

  updatePage(id: number, page: Page): Observable<Page> {
    return this.http.put<Page>(`${this.apiUrl}/${id}`, page).pipe(
      tap(() => this.refreshPages$.next())
    );
  }

  deletePage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.refreshPages$.next())
    );
  }
}
