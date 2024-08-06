import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RoleRequest } from '../../model/request/role-request';
import { ApiResponse } from '../../model/response/api-response';
import { RoleResponse } from '../../model/response/role-response';
import { environment } from '../../../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class RoleApiService {
  private apiUrl = `${environment.apiUrl}/roles`;

  constructor(private http: HttpClient) { }

  create(roleRequest: RoleRequest): Observable<ApiResponse<RoleResponse>> {
    return this.http.post<ApiResponse<RoleResponse>>(this.apiUrl, roleRequest)
  }

  findById(roleRequestId: number): Observable<ApiResponse<RoleResponse>> {
    const url = `${this.apiUrl}/${roleRequestId}`;
    return this.http.get<ApiResponse<RoleResponse>>(url);
  }

  findAll(): Observable<ApiResponse<RoleResponse[]>> {
    return this.http.get<ApiResponse<RoleResponse[]>>(this.apiUrl);
  }

  update(roleRequestId: number, roleRequest: RoleRequest): Observable<ApiResponse<RoleResponse>> {
    const url = `${this.apiUrl}/${roleRequestId}`;
    return this.http.put<ApiResponse<RoleResponse>>(url, roleRequest);
  }

  delete(roleRequestId: number): Observable<ApiResponse<RoleResponse>> {
    const url = `${this.apiUrl}/${roleRequestId}`;
    return this.http.delete<ApiResponse<RoleResponse>>(url);
  }
}
