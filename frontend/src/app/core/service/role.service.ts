import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { Observable } from 'rxjs';
import { Role } from '../interface/model/role';
import { ApiResponse } from '../interface/response/apiResponse';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private apiURL = API_URL + 'api/v1/role'

  constructor(private http: HttpClient) { }

  create<T>(role: Role): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(this.apiURL, role)
  }

  findByID<T>(id: number): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.get<ApiResponse<T>>(url);
  }

  findAll<T>(): Observable<ApiResponse<T>> {
    return this.http.get<ApiResponse<T>>(this.apiURL);
  }

  update<T>(id: string, role: Role): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.put<ApiResponse<T>>(url, role);
  }

  delete<T>(id: string): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.delete<ApiResponse<T>>(url);
  }
}
