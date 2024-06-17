export interface User {
    id?: string;
    email?: string;
    password?: string;
    fullName?: string;
    active?: boolean;
    createdDate?: string;
    createdBy?: string;
    lastModifiedDate?: string;
    lastModifiedBy?: string;
    role_id?: number;
}
