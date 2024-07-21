import { Permission } from "./permission";

export interface Role {
    id?: number;
    name?: RoleEnum;
    description?: string;
    permissions?: Permission[];
}

export enum RoleEnum {
    ADMIN = 1,
    USER = 2,
    STAFF = 3
}
