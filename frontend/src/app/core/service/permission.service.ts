import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../interface/response/apiResponse';
import { Observable } from 'rxjs';
import { PermissionRequest } from '../interface/request/permission-request';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private apiURL = API_URL + 'api/v1/permissionRequest'

  constructor(private http: HttpClient) { }

  create(permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionRequest>> {
    return this.http.post<ApiResponse<PermissionRequest>>(this.apiURL, permissionRequest)
  }

  findByID(permissionRequestID: string): Observable<ApiResponse<PermissionRequest>> {
    const url = `${this.apiURL}/${permissionRequestID}`;
    return this.http.get<ApiResponse<PermissionRequest>>(url);
  }

  findAll(): Observable<ApiResponse<PermissionRequest[]>> {
    return this.http.get<ApiResponse<PermissionRequest[]>>(this.apiURL);
  }

  update(permissionRequestID: string, permissionRequest: PermissionRequest): Observable<ApiResponse<PermissionRequest>> {
    const url = `${this.apiURL}/${permissionRequestID}`;
    return this.http.put<ApiResponse<PermissionRequest>>(url, permissionRequest);
  }

  delete(permissionRequestID: string): Observable<ApiResponse<PermissionRequest>> {
    const url = `${this.apiURL}/${permissionRequestID}`;
    return this.http.delete<ApiResponse<PermissionRequest>>(url);
  }
}
