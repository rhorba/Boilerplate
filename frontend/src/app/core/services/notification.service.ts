import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Notification {
    message: string;
    type: 'success' | 'error' | 'info' | 'warning';
    id: number;
}

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private notificationsSubject = new BehaviorSubject<Notification[]>([]);
    notifications$ = this.notificationsSubject.asObservable();
    private counter = 0;

    constructor() { }

    show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info') {
        const id = this.counter++;
        const notification: Notification = { message, type, id };
        const current = this.notificationsSubject.value;
        this.notificationsSubject.next([...current, notification]);

        setTimeout(() => {
            this.remove(id);
        }, 3000); // Auto dismiss after 3 seconds
    }

    success(message: string) {
        this.show(message, 'success');
    }

    error(message: string) {
        this.show(message, 'error');
    }

    info(message: string) {
        this.show(message, 'info');
    }

    warning(message: string) {
        this.show(message, 'warning');
    }

    remove(id: number) {
        const current = this.notificationsSubject.value;
        this.notificationsSubject.next(current.filter(n => n.id !== id));
    }
}
