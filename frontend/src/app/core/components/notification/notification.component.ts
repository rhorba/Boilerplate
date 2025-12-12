import { Component } from '@angular/core';
import { NotificationService, Notification } from '../../services/notification.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-notification',
    templateUrl: './notification.component.html',
    styleUrls: ['./notification.component.css']
})
export class NotificationComponent {
    notifications$: Observable<Notification[]>;

    constructor(private notificationService: NotificationService) {
        this.notifications$ = this.notificationService.notifications$;
    }

    remove(id: number) {
        this.notificationService.remove(id);
    }
}
