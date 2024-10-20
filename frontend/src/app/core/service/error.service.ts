import { Injectable } from '@angular/core';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor() { }

  public handleError(operation: string) {
    return (error: any) => {
      console.error(`Error ${operation}`, error);
      return throwError(() => new Error(error.message || 'Server error'));
    };
  }
}
