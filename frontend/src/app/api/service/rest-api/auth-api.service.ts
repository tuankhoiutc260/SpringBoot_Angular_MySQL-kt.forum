import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthenticationRequest } from '../../model/request/authentication-request';
import { IntrospectRequest } from '../../model/request/introspect-request';
import { ApiResponse } from '../../model/response/api-response';
import { LogoutRequest } from '../../model/request/logout-request';
import { environment } from '../../../../environments/environment';
import { AuthenticationResponse } from '../../model/response/authenticated-response';
import { IntrospectResponse } from '../../model/response/introspect-request';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  constructor(
    private http: HttpClient
  ) { }

  login(authenticationRequest: AuthenticationRequest): Observable<AuthenticationResponse> {
    return this.http.post<ApiResponse<AuthenticationResponse>>(`${this.apiUrl}/login`, authenticationRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  logout(logoutRequest: LogoutRequest): Observable<void> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/logout`, logoutRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  introspect(introspectRequest: IntrospectRequest): Observable<IntrospectResponse> {
    return this.http.post<ApiResponse<IntrospectResponse>>(`${this.apiUrl}/introspect`, introspectRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error(error.message || 'Server error'));
  }
}
