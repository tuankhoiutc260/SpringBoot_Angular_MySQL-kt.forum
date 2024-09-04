export interface UserRequest {
    userName: string;
    password: string;
    email: string;
    fullName?: string;
    imageFile?: File | null;
    active?: boolean;
    roleId?: number;
}
