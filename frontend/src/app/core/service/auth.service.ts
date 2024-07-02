import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationRequest } from '../interface/request/authentication-request';
import { ApiResponse } from '../interface/response/apiResponse';
import { AuthenticationResponse } from '../interface/response/authenticated-response';
import { IntrospectResponse } from '../interface/response/introspect-request';
import { IntrospectRequest } from '../interface/request/introspect-request';
import { jwtDecode } from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiURL = API_URL + 'api/v1/auth';

  constructor(private http: HttpClient) { }

  login(authenticationRequest: AuthenticationRequest): Observable<ApiResponse<AuthenticationResponse>> {
    return this.http.post<ApiResponse<AuthenticationResponse>>(`${this.apiURL}/login`, authenticationRequest);
  }

  introspect(introspectRequest: IntrospectRequest): Observable<ApiResponse<IntrospectResponse>> {
    return this.http.post<ApiResponse<IntrospectResponse>>(`${this.apiURL}/introspect`, introspectRequest);
  }

  // TOKEN
  setToken(token: string) {
    window.localStorage.setItem("auth_token", token);
  }

  getToken(): string {
    return window.localStorage.getItem("auth_token") || '';
  }

  removeToken(): void {
    window.localStorage.removeItem("auth_token");
  }

  // USERNAME
  setCurrentUserName(userName: string) {
    window.localStorage.setItem("user_name", userName);
  }

  getCurrentUserName(): string {
    return window.localStorage.getItem("user_name") || '';
  }

  removeCurrentUserName(): void {
    window.localStorage.removeItem("user_name");
  }

  // PASSWORD
  setCurrentPassword(password: string) {
    window.localStorage.setItem("password", password);
  }

  getCurrentPassword(): string {
    return window.localStorage.getItem("password") || '';
  }

  removeCurrentPassword(): void {
    window.localStorage.removeItem("password");
  }

  // Check if user is authenticated
  canActive(): boolean {
    return !!this.getToken();
  }


  getRole(): string {
    const token = this.getToken();
    if (token) {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.scope || 'ROLE_USER';
    }
    return 'ROLE_USER';
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
