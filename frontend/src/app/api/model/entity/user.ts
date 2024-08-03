import { Role } from "./role";

export interface User {
    id?: string;
    userName?: string;
    password?: string;
    email?: string;
    fullName?: string
    image?: string
    active?: boolean;
    createdBy?: string;
    createdDate?: Date | string;
    lastModifiedBy?: string
    lastModifiedDate?: Date | string;
    role?: Role
}