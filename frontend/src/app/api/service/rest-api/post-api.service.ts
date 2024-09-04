import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { PostRequest } from '../../model/request/post-request';
import { ApiResponse } from '../../model/response/api-response';
import { PostResponse } from '../../model/response/post-response';
import { PagedResponse } from '../../model/response/paged-response';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostApiService {
  private readonly apiUrl = `${environment.apiUrl}/posts`;

  constructor(
    private http: HttpClient
  ) { }

  create(postRequest: PostRequest): Observable<PostResponse> {
    return this.http.post<ApiResponse<PostResponse>>(this.apiUrl, postRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(postId: string): Observable<PostResponse> {
    return this.http.get<ApiResponse<PostResponse>>(`${this.apiUrl}/id/${postId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getBySubCategoryId(subCategoryId: string, page: number, size: number): Observable<PagedResponse<PostResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}/sub-category/${subCategoryId}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<PostResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(postId: string, postRequest: PostRequest): Observable<PostResponse> {
    return this.http.put<ApiResponse<PostResponse>>(`${this.apiUrl}/${postId}`, postRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(postId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  incrementViewCount(postId: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${postId}/views`, null)
      .pipe(
        catchError(this.handleError)
      );
  }

  save(postId: string | null, postRequest: PostRequest): Observable<PostResponse> {
    if (postId) {
      return this.update(postId, postRequest);
    } else {
      return this.create(postRequest);
    }
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(() => new Error(error.message || 'Server error'));
  }
}
