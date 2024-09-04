import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse } from '../../model/response/api-response';
import { PostLikeRequest } from '../../model/request/post-like-request';
import { PostLikeResponse } from '../../model/response/post-like-response';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostLikeApiService {
  private readonly apiUrl = `${environment.apiUrl}/likes`;

  constructor(
    private http: HttpClient
  ) { }

  toggle(likeRequest: PostLikeRequest): Observable<PostLikeResponse> {
    return this.http.post<ApiResponse<PostLikeResponse>>(`${this.apiUrl}/toggle`, likeRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  isLiked(postLikeRequest: PostLikeRequest): Observable<boolean> {
    return this.http.post<ApiResponse<boolean>>(`${this.apiUrl}/is-liked`, postLikeRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  countLikes(postLikeRequest: PostLikeRequest): Observable<number> {
    return this.http.post<ApiResponse<number>>(`${this.apiUrl}/count-likes`, postLikeRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error(error.message || 'Server error'));
  }
}
