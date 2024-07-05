import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { Observable } from 'rxjs';
import { ApiResponse } from '../interface/response/apiResponse';
import { PostRequest } from '../interface/request/post-request';
import { PostResponse } from '../interface/response/post-response';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiURL = API_URL + 'api/v1/posts'

  constructor(private http: HttpClient) { }

  create(postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    return this.http.post<ApiResponse<PostResponse>>(this.apiURL, postRequest);
  }

  findByID(postRequestID: string): Observable<ApiResponse<PostResponse>> {
    const url = `${this.apiURL}/${postRequestID}`;
    return this.http.get<ApiResponse<PostResponse>>(url);
  }

  findAll(): Observable<ApiResponse<PostResponse[]>> {
    return this.http.get<ApiResponse<PostResponse[]>>(this.apiURL);
  }

  update(postRequestID: string, postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    const url = `${this.apiURL}/${postRequestID}`;
    return this.http.put<ApiResponse<PostResponse>>(url, postRequest);
  }

  delete(postRequestID: string): Observable<ApiResponse<PostResponse>> {
    const url = `${this.apiURL}/${postRequestID}`;
    return this.http.delete<ApiResponse<PostResponse>>(url);
  }

  save(postRequestID: string | null, postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    if (postRequestID) {
      return this.update(postRequestID, postRequest);
    } else {
      return this.create(postRequest);
    }
  }
}