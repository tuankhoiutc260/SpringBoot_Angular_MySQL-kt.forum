import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthenticationRequest } from '../interface/request/authentication-request';
import { AuthenticationResponse } from '../interface/response/authenticated-response';
import { ApiResponse } from '../interface/response/apiResponse';
import { IntrospectRequest } from '../interface/request/introspect-request';
import { IntrospectResponse } from '../interface/response/introspect-request';
import { API_URL } from '../../core/config/config';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private apiURL = API_URL + 'api/v1/auth';

  constructor(private http: HttpClient) { } 

  login(authenticationRequest: AuthenticationRequest): Observable<ApiResponse<AuthenticationResponse>> {
    return this.http.post<ApiResponse<AuthenticationResponse>>(`${this.apiURL}/login`, authenticationRequest);
  }

  introspect(introspectRequest: IntrospectRequest): Observable<ApiResponse<IntrospectResponse>> {
    return this.http.post<ApiResponse<IntrospectResponse>>(`${this.apiURL}/introspect`, introspectRequest);
  }
}
