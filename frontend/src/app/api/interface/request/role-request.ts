import { RoleEnum } from "../model/role";

export interface RoleRequest {
    name?: RoleEnum;
    description?: string;
    permissions?: number[];
}
