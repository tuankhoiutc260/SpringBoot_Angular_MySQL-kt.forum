import { PermissionResponse } from "./permission-response";

export interface RoleResponse {
    id: number;
    name: string;
    description: string;
    permissions: PermissionResponse[];
}
