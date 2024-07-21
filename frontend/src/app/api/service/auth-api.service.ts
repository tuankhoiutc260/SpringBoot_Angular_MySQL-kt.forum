import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../../core/config/config';
import { Observable } from 'rxjs';
import { AuthenticationRequest } from '../model/request/authentication-request';
import { IntrospectRequest } from '../model/request/introspect-request';
import { ApiResponse } from '../model/response/apiResponse';
import { AuthenticationResponse } from '../model/response/authenticated-response';
import { IntrospectResponse } from '../model/response/introspect-request';
import { LogoutRequest } from '../model/request/logout-request';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private apiURL = API_URL + 'api/v1/auth';

  constructor(private http: HttpClient) { } 

  login(authenticationRequest: AuthenticationRequest): Observable<ApiResponse<AuthenticationResponse>> {
    return this.http.post<ApiResponse<AuthenticationResponse>>(`${this.apiURL}/login`, authenticationRequest);
  }

  logout(logoutRequest: LogoutRequest): Observable<ApiResponse<void>>{
    return this.http.post<ApiResponse<void>>(`${this.apiURL}/logout`, logoutRequest)
  }

  introspect(introspectRequest: IntrospectRequest): Observable<ApiResponse<IntrospectResponse>> {
    return this.http.post<ApiResponse<IntrospectResponse>>(`${this.apiURL}/introspect`, introspectRequest);
  }
}
