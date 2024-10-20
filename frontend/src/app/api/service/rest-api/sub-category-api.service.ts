import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { SubCategoryRequest } from '../../model/request/sub-category-request';
import { ApiResponse } from '../../model/response/api-response';
import { SubCategoryResponse } from '../../model/response/sub-category-response';
import { PagedResponse } from '../../model/response/paged-response';

@Injectable({
  providedIn: 'root'
})
export class SubCategoryApiService {
  private readonly apiUrl = `${environment.apiUrl}/sub-categories`;

  constructor(
    private http: HttpClient
  ) { }

  create(subCategoryRequest: SubCategoryRequest): Observable<SubCategoryResponse> {
    const subCategoryFormData = new FormData();
    subCategoryFormData.append('title', subCategoryRequest.title!);
    subCategoryFormData.append('description', subCategoryRequest.description!);
    subCategoryFormData.append('coverImageFile', subCategoryRequest.coverImageFile!);
    subCategoryFormData.append('categoryId', subCategoryRequest.categoryId!);
    return this.http.post<ApiResponse<SubCategoryResponse>>(this.apiUrl, subCategoryFormData, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(subCategoryId: string): Observable<SubCategoryResponse> {
    return this.http.get<ApiResponse<SubCategoryResponse>>(`${this.apiUrl}/${subCategoryId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getByCategoryId(categoryId: string, page: number, size: number): Observable<PagedResponse<SubCategoryResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<SubCategoryResponse[]>>>(`${this.apiUrl}/category/${categoryId}`, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<SubCategoryResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<SubCategoryResponse[]>>>(this.apiUrl, { params })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(subCategoryId: string, subCategoryRequest: SubCategoryRequest): Observable<SubCategoryResponse> {
    const subCategoryFormData = new FormData();
    subCategoryFormData.append('title', subCategoryRequest.title!);
    subCategoryFormData.append('description', subCategoryRequest.description!);
    subCategoryFormData.append('coverImageFile', subCategoryRequest.coverImageFile!);
    subCategoryFormData.append('categoryId', subCategoryRequest.categoryId!);
    return this.http.put<ApiResponse<SubCategoryResponse>>(`${this.apiUrl}/${subCategoryId}`, subCategoryFormData, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(subCategoryId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${subCategoryId}`, { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  search(query: string, page: number, size: number): Observable<PagedResponse<SubCategoryResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<SubCategoryResponse[]>>>(`${this.apiUrl}/${query}`, { params })
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
