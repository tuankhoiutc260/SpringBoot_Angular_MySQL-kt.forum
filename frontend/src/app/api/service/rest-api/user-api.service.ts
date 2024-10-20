import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { UserRequest } from '../../model/request/user-request';
import { ApiResponse } from '../../model/response/api-response';
import { UserResponse } from '../../model/response/user-response';
import { environment } from '../../../../environments/environment';
import { PagedResponse } from '../../model/response/paged-response';
import { ChangePasswordRequest } from '../../model/request/change-pasword-request';
import { UpdateProfileRequest } from '../../model/request/change-pasword-request copy';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(
    private http: HttpClient
  ) { }

  create(userRequest: UserRequest): Observable<UserResponse> {
    const userFormData = new FormData();
    userFormData.append('userName', userRequest.userName!);
    userFormData.append('password', userRequest.password!);
    userFormData.append('email', userRequest.email!);
    return this.http.post<ApiResponse<UserResponse>>(this.apiUrl, userFormData)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getById(userId: string | null): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/id/${userId}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getByUserName(userRequestUserName: string): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/username/${userRequestUserName}`)
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getMyInfo(): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/my-info`, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  getAll(page: number, size: number): Observable<PagedResponse<UserResponse[]>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<UserResponse[]>>>(`${this.apiUrl}`, { params, withCredentials: true })
      .pipe(
        tap(result => console.log(result)),
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  update(userId: string, userRequest: UserRequest): Observable<UserResponse> {
    const userFormData = new FormData()
    if (userRequest.userName) {
      userFormData.append('userName', userRequest.userName);
    }

    if (userRequest.fullName) {
      userFormData.append('fullName', userRequest.fullName);
    }

    if (userRequest.password) {
      userFormData.append('password', userRequest.password);
    }

    if (userRequest.email) {
      userFormData.append('email', userRequest.email);
    }

    if (userRequest.imageFile) {
      userFormData.append('imageFile', userRequest.imageFile);
    }

    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/${userId}`, userFormData, { withCredentials: true })
      .pipe(
        map(apiResponse => apiResponse.result!),
        catchError(this.handleError)
      );
  }

  deleteById(userId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}`, { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  save(userRequestId: string | null, userRequest: UserRequest): Observable<UserResponse> {
    if (userRequestId) {
      return this.update(userRequestId, userRequest);
    } else {
      return this.create(userRequest);
    }
  }

  changePassword(userId: string, changePasswordRequest: ChangePasswordRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/change-password/${userId}`, changePasswordRequest, { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  updateProfile(userId: string, updateProfileRequest: UpdateProfileRequest): Observable<void> {
    const updateProfileFormData = new FormData()
    // if (updateProfileRequest.fullName) {
    //   updateProfileFormData.append('fullName', updateProfileRequest.fullName);
    // }

    // if (updateProfileRequest.aboutMe) {
    //   updateProfileFormData.append('aboutMe', updateProfileRequest.aboutMe);
    // }



    updateProfileFormData.append('fullName', updateProfileRequest.fullName);
    if (updateProfileRequest.imageFile) {
      updateProfileFormData.append('imageFile', updateProfileRequest.imageFile);
    } updateProfileFormData.append('aboutMe', updateProfileRequest.aboutMe);


    return this.http.put<void>(`${this.apiUrl}/profile/${userId}`, updateProfileFormData, { withCredentials: true })
      .pipe(
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
