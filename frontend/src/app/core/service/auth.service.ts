import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiURL = API_URL + 'api/v1/auth'
  constructor(private http: HttpClient) { }

  login(credentials: { userName: any, password: any }): Observable<any> {
    return this.http.post<any>(`${this.apiURL}/login`, credentials);
  }

  //TOKEN
  setToken(token: string) {
    window.localStorage.setItem("auth_token", token);
  }

  getToken(): string {
    return window.localStorage.getItem("auth_token") || '';
  }

  removeToken(): void {
    window.localStorage.removeItem("auth_token");
  }

  //USERNAME
  setCurrentUserName(userName: string) {
    window.localStorage.setItem("user_name", userName);
  }

  getCurrentUserName(): string {
    console.log(window.localStorage)
    return window.localStorage.getItem("user_name") || '';
  }

  removeCurrentUserName(): void {
    window.localStorage.removeItem("user_name");
  }

  //PASSWORD
  setCurrentPassword(password: string) {
    window.localStorage.setItem("password", password);
  }

  getCurrentPassword(): string {
    return window.localStorage.getItem("password") || '';
  }

  removeCurrentPassword(): void {
    window.localStorage.removeItem("password");
  }

}
