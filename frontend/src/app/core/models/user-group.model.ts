import { User } from './user.model';
import { Page } from './page.model';

export interface UserGroup {
    id: number;
    name: string;
    description?: string;
    users?: User[];
    pages?: Page[];
}
