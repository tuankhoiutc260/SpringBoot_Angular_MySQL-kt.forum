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
    return this.http.post<ApiResponse<RoleResponse>>(this.apiUrl, roleRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(roleId: number): Observable<RoleResponse> {
    return this.http.get<ApiResponse<RoleResponse>>(`${this.apiUrl}/${roleId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<RoleResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<RoleResponse[]>>>(this.apiUrl, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(roleId: number, roleRequest: RoleRequest): Observable<RoleResponse> {
    return this.http.put<ApiResponse<RoleResponse>>(`${this.apiUrl}/${roleId}`, roleRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(roleId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${roleId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error(error.message || 'Server error'));
  }
}
