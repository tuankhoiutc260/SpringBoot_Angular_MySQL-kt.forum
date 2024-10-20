export interface UserRequest {
    userName: string | null;
    password: string | null;
    email: string | null;
    fullName: string | null;
    imageFile?: File | null;
    aboutMe: string;
    active?: boolean;
    roleId?: number;
}
