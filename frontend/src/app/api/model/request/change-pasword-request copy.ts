export interface UpdateProfileRequest {
    fullName:string;
    imageFile?: File | null;
    aboutMe: string;
}
