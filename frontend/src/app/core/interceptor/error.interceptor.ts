import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { ApiResponse } from '../../api/model/response/api-response';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  readonly LOGIN_ERROR_CODE = 3010;

  constructor(
    private router: Router,
  ) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          const apiResponse: ApiResponse<any> = error.error;

          if(apiResponse && apiResponse.code !== this.LOGIN_ERROR_CODE){
            this.router.navigate(['/login'], {
              queryParams: {
                message: error.status === 403 ? 'Please login to Administrator account!' : 'Login expired, please log in again!'
              }
            });
          }
        }
        // else if (error.status === 404) {
        //   this.router.navigate(['/404']);
        // }
        return throwError(() => error);
      })
    );
  }
}
