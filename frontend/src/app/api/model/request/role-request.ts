import { RoleEnum } from "../entity/role";

export interface RoleRequest {
    name?: RoleEnum;
    description?: string;
    permissions?: number[];
}
