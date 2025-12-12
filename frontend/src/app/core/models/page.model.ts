import { UserGroup } from './user-group.model';

export interface Page {
    id?: number;
    title: string;
    slug: string;
    content: string;
    icon?: string;
    roles?: string;
    schema?: string;
    accessControl?: string; // JSON
    groups?: UserGroup[];
}
