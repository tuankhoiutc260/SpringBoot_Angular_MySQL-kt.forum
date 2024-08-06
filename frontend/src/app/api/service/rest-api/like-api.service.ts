import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LikeRequest } from '../../model/request/like-request';
import { ApiResponse } from '../../model/response/api-response';
import { LikeResponse } from '../../model/response/like-response';
import { environment } from '../../../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class LikeApiService {
  private apiUrl = `${environment.apiUrl}/likes`;

  constructor(private http: HttpClient) { }

  toggle(likeRequest: LikeRequest): Observable<ApiResponse<LikeResponse>> {
    return this.http.post<ApiResponse<LikeResponse>>(`${this.apiUrl}/toggle`, likeRequest);
  }

  isLiked(likeRequest: LikeRequest): Observable<ApiResponse<boolean>> {
    const params = new HttpParams()
      .set('postId', likeRequest.postId!);

    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/is-liked`, { params });
  }

  countLikes(postId: string): Observable<ApiResponse<number>> {
    const params = new HttpParams()
      .set('postId', postId);

    return this.http.get<ApiResponse<number>>(`${this.apiUrl}/count-likes`, { params });
  }
}
