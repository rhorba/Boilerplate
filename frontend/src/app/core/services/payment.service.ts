import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CheckoutRequest {
    priceId: string;
    successUrl: string;
    cancelUrl: string;
}

export interface CheckoutResponse {
    url: string;
}

@Injectable({
    providedIn: 'root'
})
export class PaymentService {
    private apiUrl = `${environment.apiUrl}/payments`;

    constructor(private http: HttpClient) { }

    createCheckoutSession(priceId: string): Observable<CheckoutResponse> {
        const body: CheckoutRequest = {
            priceId,
            successUrl: window.location.origin + '/settings?payment=success',
            cancelUrl: window.location.origin + '/settings?payment=cancel'
        };
        return this.http.post<CheckoutResponse>(`${this.apiUrl}/checkout`, body);
    }
}
