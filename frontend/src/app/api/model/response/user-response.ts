import { RoleResponse } from "./role-response";

export interface UserResponse {
    id: string;
    userName: string;
    password: string;
    email: string;
    fullName: string;
    imageUrl: string;
    cloudinaryImageId: string;
    active: boolean;
    createdBy: string;
    createdDate: Date | string;
    lastModifiedBy: string;
    lastModifiedDate: Date | string;
    role: RoleResponse;
}
