import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RoleRequest } from '../../model/request/role-request';
import { ApiResponse } from '../../model/response/api-response';
import { RoleResponse } from '../../model/response/role-response';
import { environment } from '../../../../environments/environment';
import { PagedResponse } from '../../model/response/paged-response';

@Injectable({
  providedIn: 'root'
})
export class RoleApiService {
  private readonly apiUrl = `${environment.apiUrl}/roles`;

  constructor(
    private http: HttpClient
  ) { }

  create(roleRequest: RoleRequest): Observable<RoleResponse> {
    return this.http.post<ApiResponse<RoleResponse>>(this.apiUrl, roleRequest, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(roleId: number): Observable<RoleResponse> {
    return this.http.get<ApiResponse<RoleResponse>>(`${this.apiUrl}/${roleId}`, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<RoleResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<RoleResponse[]>>>(this.apiUrl, { params, withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(roleId: number, roleRequest: RoleRequest): Observable<RoleResponse> {
    return this.http.put<ApiResponse<RoleResponse>>(`${this.apiUrl}/${roleId}`, roleRequest, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(roleId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${roleId}`, { withCredentials: true })
      .pipe(
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
