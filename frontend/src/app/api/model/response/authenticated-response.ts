export interface AuthenticationResponse {
    userId: string;
    accessToken: string;
    refreshToken: string;
    authenticated: boolean;
}
