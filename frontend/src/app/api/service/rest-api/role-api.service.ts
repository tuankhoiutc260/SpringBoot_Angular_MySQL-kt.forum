import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../../../core/config/config';
import { RoleRequest } from '../../model/request/role-request';
import { ApiResponse } from '../../model/response/api-response';
import { RoleResponse } from '../../model/response/role-response';

@Injectable({
  providedIn: 'root'
})
export class RoleApiService {
  private apiURL = API_URL + 'roles'

  constructor(private http: HttpClient) { }

  create(roleRequest: RoleRequest): Observable<ApiResponse<RoleResponse>> {
    return this.http.post<ApiResponse<RoleResponse>>(this.apiURL, roleRequest)
  }

  findById(roleRequestId: number): Observable<ApiResponse<RoleResponse>> {
    const url = `${this.apiURL}/${roleRequestId}`;
    return this.http.get<ApiResponse<RoleResponse>>(url);
  }

  findAll(): Observable<ApiResponse<RoleResponse[]>> {
    return this.http.get<ApiResponse<RoleResponse[]>>(this.apiURL);
  }

  update(roleRequestId: number, roleRequest: RoleRequest): Observable<ApiResponse<RoleResponse>> {
    const url = `${this.apiURL}/${roleRequestId}`;
    return this.http.put<ApiResponse<RoleResponse>>(url, roleRequest);
  }

  delete(roleRequestId: number): Observable<ApiResponse<RoleResponse>> {
    const url = `${this.apiURL}/${roleRequestId}`;
    return this.http.delete<ApiResponse<RoleResponse>>(url);
  }
}
