export interface Role {
    id?:number;
    name?:RoleEnum;
    description?:string;
}

export enum RoleEnum{
    ADMIN = 1,
    USER = 2,
    STAFF = 3
}
