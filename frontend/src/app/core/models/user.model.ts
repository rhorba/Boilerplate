import { Role } from './role.model';
import { UserGroup } from './user-group.model';
import { Action } from '../services/action.service';

export interface User {
    id?: number;
    email: string;
    role?: Role;
    firstname?: string;
    lastname?: string;
    password?: string;
    groups?: UserGroup[];
    actions?: Action[];
}
