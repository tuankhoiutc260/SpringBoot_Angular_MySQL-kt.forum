import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { PermissionRequest } from '../../model/request/permission-request';
import { ApiResponse } from '../../model/response/api-response';
import { PermissionResponse } from '../../model/response/permission-response';
import { environment } from '../../../../environments/environment';
import { PagedResponse } from '../../model/response/paged-response';

@Injectable({
  providedIn: 'root'
})
export class PermissionApiService {
  private apiUrl = `${environment.apiUrl}/permissions`;

  constructor(
    private http: HttpClient
  ) { }

  create(permissionRequest: PermissionRequest): Observable<PermissionResponse> {
    return this.http.post<ApiResponse<PermissionResponse>>(this.apiUrl, permissionRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<PermissionResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<PermissionResponse[]>>>(`${this.apiUrl}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(permissionId: string, permissionRequest: PermissionRequest): Observable<PermissionResponse> {
    return this.http.put<ApiResponse<PermissionResponse>>(`${this.apiUrl}/${permissionId}`, permissionRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(permissionId: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${permissionId}`)
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
