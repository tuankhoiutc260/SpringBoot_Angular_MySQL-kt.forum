import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { PostRequest } from '../../model/request/post-request';
import { ApiResponse } from '../../model/response/api-response';
import { PostResponse } from '../../model/response/post-response';
import { PagedResponse } from '../../model/response/paged-response';
import { environment } from '../../../../environments/environment';
import { SubCategoryRankResponse } from '../../model/response/sub-category-rank-response';
import { UserRankResponse } from '../../model/response/user-rank-response';
import { SearchType } from '../../model/enum/search-type';

@Injectable({
  providedIn: 'root'
})
export class PostApiService {
  private readonly apiUrl = `${environment.apiUrl}/posts`;

  constructor(
    private http: HttpClient
  ) { }

  create(postRequest: PostRequest): Observable<PostResponse> {
    return this.http.post<ApiResponse<PostResponse>>(this.apiUrl, postRequest, { withCredentials: true })
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
    return this.http.put<ApiResponse<PostResponse>>(`${this.apiUrl}/${postId}`, postRequest, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
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

  deleteById(postId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}`, { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  searchBySubCategoryId(query: string, subCategoryId: string, page: number, size: number, searchType: SearchType): Observable<PagedResponse<PostResponse[]>> {
    const params = new HttpParams()
      .set('query', query)
      .set('subCategoryId', subCategoryId)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('searchType', searchType);

    return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}/searchBySubCategoryId`, { params, withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(error => {
          console.error("Error during search", error);
          return this.handleError(error);
        })
      );
  }

  // search(query: string, page: number, size: number): Observable<PagedResponse<PostResponse[]>> {
  //   const params = new HttpParams()
  //     .set('query', query)
  //     .set('page', page.toString())
  //     .set('size', size.toString());

  //   return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}/search`, { params, withCredentials: true })
  //     .pipe(
  //       map(apiResponse => apiResponse.result!),
  //       catchError(error => {
  //         console.error("error", error);
  //         return this.handleError(error);
  //       })
  //     );
  // }

  incrementViewCount(postId: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${postId}/views`, null)
      .pipe(
        catchError(this.handleError)
      );
  }

  getTop3OrderByLikesDesc(): Observable<PostResponse[]> {
    return this.http.get<ApiResponse<PostResponse[]>>(`${this.apiUrl}/top-3`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getPostsLiked(userId: string, page: number, size: number): Observable<PagedResponse<PostResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}/posts-liked/${userId}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getPostsLikedBySubCategoryId(userId: string, subCategoyId: string, page: number, size: number): Observable<PagedResponse<PostResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}/posts-liked/${userId}/sub-category/${subCategoyId}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getPostsByCreatedBy(userId: string, page: number, size: number): Observable<PagedResponse<PostResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<PostResponse[]>>>(`${this.apiUrl}/posts-created-by/${userId}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  get5MostRecentlyCreatedPosts(): Observable<PostResponse[]> {
    return this.http.get<ApiResponse<PostResponse[]>>(`${this.apiUrl}/5-most-recently-created-post`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getTop3UserRanks(): Observable<UserRankResponse[]> {
    return this.http.get<ApiResponse<UserRankResponse[]>>(`${this.apiUrl}/user-rank`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getTheTop10MostSubCategory(): Observable<SubCategoryRankResponse[]> {
    return this.http.get<ApiResponse<SubCategoryRankResponse[]>>(`${this.apiUrl}/sub-category-rank`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  get6RandomPosts(postId: string | null): Observable<PostResponse[]> {
    return this.http.get<ApiResponse<PostResponse[]>>(`${this.apiUrl}/random-post/${postId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // Lỗi phía client
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      // Lỗi phía server
      errorMessage = `Server-side error: ${error.status} ${error.message}`;
    }
    console.error('Error occurred:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
