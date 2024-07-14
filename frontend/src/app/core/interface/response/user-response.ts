import { RoleResponse } from "./role-response";

export interface UserResponse {
    id?: string;
    userName?: string;
    password?: string;
    email?: string;
    fullName?: string;
    active?: boolean;
    createdDate?: Date | string;
    createdBy?: string;
    lastModifiedDate?: Date | string;
    lastModifiedBy?: string;
    image?:string;
    role?: RoleResponse
}
