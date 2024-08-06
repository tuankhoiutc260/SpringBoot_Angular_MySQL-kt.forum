import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PermissionRequest } from '../../model/request/permission-request';
import { ApiResponse } from '../../model/response/api-response';
import { PermissionResponse } from '../../model/response/permission-response';
import { environment } from '../../../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class PermissionApiService {
  private apiUrl = `${environment.apiUrl}/permissions`;

  constructor(private http: HttpClient) { }

  create(permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionResponse>> {
    return this.http.post<ApiResponse<PermissionResponse>>(this.apiUrl, permissionRequest)
  }

  findById(permissionRequestId: string): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiUrl}/${permissionRequestId}`;
    return this.http.get<ApiResponse<PermissionResponse>>(url);
  }

  findAll(): Observable<ApiResponse<PermissionResponse[]>> {
    return this.http.get<ApiResponse<PermissionResponse[]>>(this.apiUrl);
  }

  update(permissionRequestId: string, permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiUrl}/${permissionRequestId}`;
    return this.http.put<ApiResponse<PermissionResponse>>(url, permissionRequest);
  }

  delete(permissionRequestId: string): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiUrl}/${permissionRequestId}`;
    return this.http.delete<ApiResponse<PermissionResponse>>(url);
  }
}
