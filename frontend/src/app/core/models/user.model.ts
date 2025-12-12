import { Role } from './role.model';
import { UserGroup } from './user-group.model';

export interface User {
    id?: number;
    email: string;
    role?: Role;
    firstname?: string;
    lastname?: string;
    password?: string;
    groups?: UserGroup[];
}
