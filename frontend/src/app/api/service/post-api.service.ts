import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../../core/config/config';
import { PostRequest } from '../model/request/post-request';
import { ApiResponse } from '../model/response/apiResponse';
import { PostResponse } from '../model/response/post-response';

@Injectable({
  providedIn: 'root'
})
export class PostApiService {
  private apiURL = API_URL + 'api/v1/posts'

  constructor(private http: HttpClient) { }

  create(postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    const formData = new FormData();
    if (postRequest.image) {
      formData.append('image', postRequest.image);
    }
    formData.append('title', postRequest.title!);
    formData.append('content', postRequest.content!);
    formData.append('tags', postRequest.tags!.join(','));
    return this.http.post<ApiResponse<PostResponse>>(this.apiURL, formData);
  }

  findByID(postRequestID: string): Observable<ApiResponse<PostResponse>> {
    const url = `${this.apiURL}/id/${postRequestID}`;
    return this.http.get<ApiResponse<PostResponse>>(url);
  }

  findByCreatedBy(userID: string): Observable<ApiResponse<PostResponse[]>> {
    const url = `${this.apiURL}/user-post/${userID}`;
    return this.http.get<ApiResponse<PostResponse[]>>(url);  }
  

  findTop10ByOrderByLikesDesc(): Observable<ApiResponse<PostResponse[]>> {
    const url = `${this.apiURL}/top10`;
    return this.http.get<ApiResponse<PostResponse[]>>(url);
  }

  findPostsLiked(userID: string): Observable<ApiResponse<PostResponse[]>> {
    const url = `${this.apiURL}/posts-liked/${userID}`;;
    return this.http.get<ApiResponse<PostResponse[]>>(url);
  }

  findAll(): Observable<ApiResponse<PostResponse[]>> {
    return this.http.get<ApiResponse<PostResponse[]>>(this.apiURL);
  }
  
  update(postRequestID: string, postRequest: PostRequest): Observable<ApiResponse<PostResponse>> {
    const formData = new FormData();
    if (postRequest.image) {
      formData.append('image', postRequest.image);
    }
    formData.append('title', postRequest.title!);
    formData.append('content', postRequest.content!);
    formData.append('tags', postRequest.tags!.join(','));
    const url = `${this.apiURL}/${postRequestID}`;
    return this.http.put<ApiResponse<PostResponse>>(url, formData);
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