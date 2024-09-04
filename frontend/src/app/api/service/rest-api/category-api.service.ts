import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { CategoryRequest } from '../../model/request/category-request';
import { ApiResponse } from '../../model/response/api-response';
import { CategoryResponse } from '../../model/response/category-response';
import { PagedResponse } from '../../model/response/paged-response';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryApiService {
  private readonly apiUrl = `${environment.apiUrl}/categories`;

  constructor(
    private http: HttpClient
  ) { }

  create(categoryRequest: CategoryRequest): Observable<CategoryResponse> {
    return this.http.post<ApiResponse<CategoryResponse>>(this.apiUrl, categoryRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(categoryId: string): Observable<CategoryResponse> {
    return this.http.get<ApiResponse<CategoryResponse>>(`${this.apiUrl}/${categoryId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<CategoryResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<CategoryResponse[]>>>(`${this.apiUrl}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(categoryId: string, categoryRequest: CategoryRequest): Observable<CategoryResponse> {
    return this.http.put<ApiResponse<CategoryResponse>>(`${this.apiUrl}/${categoryId}`, categoryRequest)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(categoryId: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${categoryId}`)
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
