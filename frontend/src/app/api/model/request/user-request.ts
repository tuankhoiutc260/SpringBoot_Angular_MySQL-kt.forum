export interface UserRequest {
    userName?: string;
    password?: string;
    email?: string;
    fullName?: string;
    image?: File | null;
    active?: boolean;
    role?: number
}
