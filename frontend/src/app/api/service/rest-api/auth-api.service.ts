import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationRequest } from '../../model/request/authentication-request';
import { IntrospectRequest } from '../../model/request/introspect-request';
import { ApiResponse } from '../../model/response/api-response';
import { AuthenticationResponse } from '../../model/response/authenticated-response';
import { IntrospectResponse } from '../../model/response/introspect-request';
import { LogoutRequest } from '../../model/request/logout-request';
import { environment } from '../../../../enviroments/environment';
import { API_URL } from '../../../core/config/config';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) { }

  login(authenticationRequest: AuthenticationRequest): Observable<ApiResponse<AuthenticationResponse>> {
    return this.http.post<ApiResponse<AuthenticationResponse>>(`${this.apiUrl}/login`, authenticationRequest);
  }

  logout(logoutRequest: LogoutRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/logout`, logoutRequest)
  }

  introspect(introspectRequest: IntrospectRequest): Observable<ApiResponse<IntrospectResponse>> {
    return this.http.post<ApiResponse<IntrospectResponse>>(`${this.apiUrl}/introspect`, introspectRequest);
  }
}
