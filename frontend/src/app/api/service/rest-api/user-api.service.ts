import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { UserRequest } from '../../model/request/user-request';
import { ApiResponse } from '../../model/response/api-response';
import { UserResponse } from '../../model/response/user-response';
import { environment } from '../../../../environments/environment';
import { PagedResponse } from '../../model/response/paged-response';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(
    private http: HttpClient
  ) { }

  create1(userRequest: UserRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(this.apiUrl, userRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  create(userRequest: UserRequest): Observable<UserResponse> {
    const formData = new FormData();
    formData.append('userName', userRequest.userName!);
    formData.append('password', userRequest.password!);
    formData.append('email', userRequest.email!);
    return this.http.post<ApiResponse<UserResponse>>(this.apiUrl, formData)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(userId: string): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/id/${userId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getByUserName(userRequestUserName: string): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/username/${userRequestUserName}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getMyInfo(): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/my-info`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<UserResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<UserResponse[]>>>(`${this.apiUrl}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(userId: string, userRequest: UserRequest): Observable<UserResponse> {
    const formData = new FormData();
    formData.append('userName', userRequest.userName!);
    formData.append('fullName', userRequest.fullName!);
    formData.append('password', userRequest.password!);
    formData.append('email', userRequest.email!);
    
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/${userId}`, formData)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(userId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  save(userRequestId: string | null, userRequest: UserRequest): Observable<UserResponse> {
    if (userRequestId) {
      return this.update(userRequestId, userRequest);
    } else {
      return this.create(userRequest);
    }
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error(error.message || 'Server error'));
  }
}
