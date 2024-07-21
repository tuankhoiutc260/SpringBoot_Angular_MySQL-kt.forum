import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PermissionRequest } from '../interface/request/permission-request';
import { ApiResponse } from '../interface/response/apiResponse';
import { PermissionResponse } from '../interface/response/permission-response';
import { API_URL } from '../../core/config/config';

@Injectable({
  providedIn: 'root'
})
export class PermissionApiService {
  private apiURL = API_URL + 'api/v1/permissions'

  constructor(private http: HttpClient) { }

  create(permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionResponse>> {
    return this.http.post<ApiResponse<PermissionResponse>>(this.apiURL, permissionRequest)
  }

  findByID(permissionRequestID: string): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiURL}/${permissionRequestID}`;
    return this.http.get<ApiResponse<PermissionResponse>>(url);
  }

  findAll(): Observable<ApiResponse<PermissionResponse[]>> {
    return this.http.get<ApiResponse<PermissionResponse[]>>(this.apiURL);
  }

  update(permissionRequestID: string, permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiURL}/${permissionRequestID}`;
    return this.http.put<ApiResponse<PermissionResponse>>(url, permissionRequest);
  }

  delete(permissionRequestID: string): Observable<ApiResponse<PermissionResponse>> {
    const url = `${this.apiURL}/${permissionRequestID}`;
    return this.http.delete<ApiResponse<PermissionResponse>>(url);
  }
}
