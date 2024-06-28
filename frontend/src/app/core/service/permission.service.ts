import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { HttpClient } from '@angular/common/http';
import { Permission } from '../interface/model/permission';
import { ApiResponse } from '../interface/response/apiResponse';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private apiURL = API_URL + 'api/v1/permission'

  constructor(private http: HttpClient) { }

  create<T>(permission: Permission): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(this.apiURL, permission)
  }

  findByID<T>(id: string): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.get<ApiResponse<T>>(url);
  }

  findAll<T>(): Observable<ApiResponse<T>> {
    return this.http.get<ApiResponse<T>>(this.apiURL);
  }

  update<T>(id: string, permission: Permission): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.put<ApiResponse<T>>(url, permission);
  }

  delete<T>(id: string): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.delete<ApiResponse<T>>(url);
  }
}
