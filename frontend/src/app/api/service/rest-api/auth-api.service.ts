import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthenticationRequest } from '../../model/request/authentication-request';
import { IntrospectRequest } from '../../model/request/introspect-request';
import { ApiResponse } from '../../model/response/api-response';
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
    return this.http.post<ApiResponse<AuthenticationResponse>>(`${this.apiUrl}/login`, authenticationRequest, { withCredentials: true })
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
  private isAuthenticatedStatus: boolean | null = null;

  isAuthenticated(): Observable<boolean> {
    if (this.isAuthenticatedStatus !== null) {
      return of(this.isAuthenticatedStatus);
    }

    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/is-authenticated`, { withCredentials: true })
      .pipe(
        map(apiResponse => {
          this.isAuthenticatedStatus = apiResponse.result!;
          return this.isAuthenticatedStatus;
        }),
        catchError(() => of(false))
      );
  }

  logout(): Observable<void> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  refreshAccessToken(): Observable<AuthenticatorResponse> {
    return this.http.post<ApiResponse<AuthenticatorResponse>>(`${this.apiUrl}/refresh-access-token`, {})
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // Lỗi phía client
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      // Lỗi phía server
      errorMessage = `Server-side error: ${error.status} ${error.message}`;
    }
    console.error('Error occurred:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
