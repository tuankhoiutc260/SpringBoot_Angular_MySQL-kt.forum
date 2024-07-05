import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../interface/response/apiResponse';
import { Observable } from 'rxjs';
import { PermissionRequest } from '../interface/request/permission-request';
import { PermissionResponse } from '../interface/response/permission-response';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
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
