import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../enviroments/environment';
import { SubCategoryRequest } from '../../model/request/sub-category-request';
import { ApiResponse } from '../../model/response/api-response';
import { SubCategoryResponse } from '../../model/response/sub-category-response';

@Injectable({
  providedIn: 'root'
})
export class SubCategoryApiService {
  private apiUrl = `${environment.apiUrl}/sub-categories`;

  constructor(private http: HttpClient) { }

  create(subCategoryRequest: SubCategoryRequest): Observable<ApiResponse<SubCategoryResponse>> {
    return this.http.post<ApiResponse<SubCategoryResponse>>(this.apiUrl, subCategoryRequest)
  }

  findById(subCategoryRequestId: string): Observable<ApiResponse<SubCategoryResponse>> {
    const url = `${this.apiUrl}/${subCategoryRequestId}`;
    return this.http.get<ApiResponse<SubCategoryResponse>>(url);
  }

  findByCategoryId(categoryRequestId: string, page:number, size: number): Observable<ApiResponse<SubCategoryResponse[]>> {
    const url = `${this.apiUrl}/category/${categoryRequestId}`;
    // return this.http.get<ApiResponse<SubCategoryResponse[]>>(url);
    return this.http.get<ApiResponse<SubCategoryResponse[]>>(`${this.apiUrl}/category/${categoryRequestId}?page=${page}&size=${size}`);

  }

  findAll(): Observable<ApiResponse<SubCategoryResponse[]>> {
    return this.http.get<ApiResponse<SubCategoryResponse[]>>(this.apiUrl);
  }

  update(subCategoryRequestId: string, subSubCategoryRequest: SubCategoryRequest): Observable<ApiResponse<SubCategoryResponse>> {
    const url = `${this.apiUrl}/${subCategoryRequestId}`;
    return this.http.put<ApiResponse<SubCategoryResponse>>(url, subSubCategoryRequest);
  }

  delete(subCategoryRequestId: string): Observable<ApiResponse<SubCategoryResponse>> {
    const url = `${this.apiUrl}/${subCategoryRequestId}`;
    return this.http.delete<ApiResponse<SubCategoryResponse>>(url);
  }
}
