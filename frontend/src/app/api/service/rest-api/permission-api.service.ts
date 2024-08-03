import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../../../core/config/config';
import { PermissionRequest } from '../../model/request/permission-request';
import { ApiResponse } from '../../model/response/api-response';
import { PermissionResponse } from '../../model/response/permission-response';

@Injectable({
  providedIn: 'root'
})
export class PermissionApiService {
  private apiURL = API_URL + 'permissions'

  constructor(private http: HttpClient) { }

  create(permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionResponse>> {
    return this.http.post<ApiResponse<PermissionResponse>>(this.apiURL, permissionRequest)
  }

  findById(permissionRequestId: string): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiURL}/${permissionRequestId}`;
    return this.http.get<ApiResponse<PermissionResponse>>(url);
  }

  findAll(): Observable<ApiResponse<PermissionResponse[]>> {
    return this.http.get<ApiResponse<PermissionResponse[]>>(this.apiURL);
  }

  update(permissionRequestId: string, permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiURL}/${permissionRequestId}`;
    return this.http.put<ApiResponse<PermissionResponse>>(url, permissionRequest);
  }

  delete(permissionRequestId: string): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiURL}/${permissionRequestId}`;
    return this.http.delete<ApiResponse<PermissionResponse>>(url);
  }
}
