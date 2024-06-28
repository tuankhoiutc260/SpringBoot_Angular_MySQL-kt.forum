import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { Observable } from 'rxjs';
import { User } from '../interface/model/user';
import { ApiResponse } from '../interface/response/apiResponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiURL = API_URL + 'api/v1/user'

  constructor(private http: HttpClient) { }

  create<T>(user: User): Observable<ApiResponse<T>> {
    return this.http.post<ApiResponse<T>>(this.apiURL, user)
  }

  findByID<T>(id: string): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.get<ApiResponse<T>>(url);
  }

  findAll<T>(): Observable<ApiResponse<T>> {
    return this.http.get<ApiResponse<T>>(this.apiURL);
  }

  update<T>(id: string, user: User): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.put<ApiResponse<T>>(url, user);
  }

  delete<T>(id: string): Observable<ApiResponse<T>> {
    const url = `${this.apiURL}/${id}`;
    return this.http.delete<ApiResponse<T>>(url);
  }
}
