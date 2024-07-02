import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private authService: AuthService
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    if (token) {
      return next.handle(request).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401 || error.status === 403) {
            this.router.navigate(['/login'], {
              queryParams: {
                message: error.status === 401 ? 'Unauthorized' : 'Please login to Administrator account!'
              }
            });
          }
          else if (error.status === 404) {
            this.router.navigate(['/404'])
          }
          return throwError(() => error);
        })
      );
    }
    return next.handle(request)
  }


}