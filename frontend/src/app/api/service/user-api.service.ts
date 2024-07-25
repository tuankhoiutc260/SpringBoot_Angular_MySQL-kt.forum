import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { API_URL } from '../../core/config/config';
import { UserRequest } from '../model/request/user-request';
import { ApiResponse } from '../model/response/apiResponse';
import { UserResponse } from '../model/response/user-response';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private apiURL = API_URL + 'users'

  constructor(private http: HttpClient) { }

  create(userRequest: UserRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.post<ApiResponse<UserResponse>>(this.apiURL, userRequest)
  }

  findByID(userRequestID: string): Observable<ApiResponse<UserResponse>> {
    const url = `${this.apiURL}/id/${userRequestID}`;
    return this.http.get<ApiResponse<UserResponse>>(url);
  }

  findByUserName(userRequestUserName: string): Observable<ApiResponse<UserResponse>> {
    const url = `${this.apiURL}/username/${userRequestUserName}`;
    return this.http.get<ApiResponse<UserResponse>>(url);
  }

  findAll(): Observable<ApiResponse<UserResponse[]>> {
    return this.http.get<ApiResponse<UserResponse[]>>(this.apiURL);
  }

  update(userRequestID: string, userRequest: UserRequest): Observable<ApiResponse<UserResponse>> {
    const url = `${this.apiURL}/${userRequestID}`;
    return this.http.put<ApiResponse<UserResponse>>(url, userRequest);
  }

  delete(userRequestID: string): Observable<ApiResponse<UserResponse>> {
    const url = `${this.apiURL}/${userRequestID}`;
    return this.http.delete<ApiResponse<UserResponse>>(url);
  }

  save(userRequestID: string | null, userRequest: UserRequest): Observable<ApiResponse<UserResponse>> {
    if (userRequestID) {
      return this.update(userRequestID, userRequest);
    } else {
      return this.create(userRequest);
    }
  }
}
