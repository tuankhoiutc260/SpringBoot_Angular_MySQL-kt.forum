import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserApiService } from '../../api/service/user-api.service';
import { ApiResponse } from '../../api/model/response/apiResponse';
import { UserResponse } from '../../api/model/response/user-response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isSignUpActiveSource = new BehaviorSubject<boolean>(false);
  private userLoginInfoSubject = new BehaviorSubject<UserResponse | null>(null);

  isSignUpActive$ = this.isSignUpActiveSource.asObservable();
  userLoginInfo$ = this.userLoginInfoSubject.asObservable();

  constructor(
    private userApiService: UserApiService,
    private http: HttpClient
  ) { }

  setSignUpActive(value: boolean) {
    this.isSignUpActiveSource.next(value);
  }

  getSignUpActive() {
    return this.isSignUpActiveSource.getValue();
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
  // canActive(): boolean {
  //   return !!this.getToken();
  // }

  // getRole(): string {
  //   const token = this.getToken();
  //   if (token) {
  //     const decodedToken: any = jwtDecode(token);
  //     return decodedToken.scope || 'ROLE_USER';
  //   }
  //   return 'ROLE_ANONYMOUS';
  // }

  // isAuthenticated(): boolean {
  //   return !!this.getToken();
  // }

  // New utility method to fetch and set user login info
  fetchAndSetUserLoginInfo(): void {
    const userName = this.getCurrentUserName();
    if (userName) {
      this.userApiService.findByUserName(userName).subscribe({
        next: (apiResponse: ApiResponse<UserResponse>) => {
          const userLoginInfo = apiResponse.result;
          if (userLoginInfo) {
            this.userLoginInfoSubject.next(userLoginInfo);
          } else {
            console.error('No result found in response:', apiResponse.message);
          }
        },
        error: (error) => {
          console.error('Error fetching user:', error);
        }
      });
    }
  }

  getUserLoginInfo(): Observable<UserResponse | null> {
    return this.userLoginInfo$;
  }

  getUserLoginInfoValue(): UserResponse | null {
    return this.userLoginInfoSubject.value;
  }
}
