import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../enviroments/environment';
import { CategoryRequest } from '../../model/request/category-request';
import { ApiResponse } from '../../model/response/api-response';
import { CategoryResponse } from '../../model/response/category-response';

@Injectable({
  providedIn: 'root'
})
export class CategoryApiService {
  private apiUrl = `${environment.apiUrl}/categories`;

  constructor(private http: HttpClient) { }

  create(categoryRequest: CategoryRequest): Observable<ApiResponse<CategoryResponse>> {
    return this.http.post<ApiResponse<CategoryResponse>>(this.apiUrl, categoryRequest)
  }

  findById(categoryRequestId: string): Observable<ApiResponse<CategoryResponse>> {
    const url = `${this.apiUrl}/${categoryRequestId}`;
    return this.http.get<ApiResponse<CategoryResponse>>(url);
  }

  findAll(): Observable<ApiResponse<CategoryResponse[]>> {
    return this.http.get<ApiResponse<CategoryResponse[]>>(this.apiUrl);
  }

  update(categoryRequestId: string, categoryRequest: CategoryRequest): Observable<ApiResponse<CategoryResponse>> {
    const url = `${this.apiUrl}/${categoryRequestId}`;
    return this.http.put<ApiResponse<CategoryResponse>>(url, categoryRequest);
  }

  delete(categoryRequestId: string): Observable<ApiResponse<CategoryResponse>> {
    const url = `${this.apiUrl}/${categoryRequestId}`;
    return this.http.delete<ApiResponse<CategoryResponse>>(url);
  }
}
