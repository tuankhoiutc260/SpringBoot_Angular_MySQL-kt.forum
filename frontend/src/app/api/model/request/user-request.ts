export interface UserRequest {
    userName?: string;
    password?: string;
    email?: string;
    fullName?: string;
    active?: boolean;
    image?: File | null;
    role?: number
}
