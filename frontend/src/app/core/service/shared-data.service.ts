import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UserResponse } from '../../api/model/response/user-response';

@Injectable({
  providedIn: 'root'
})
export class SharedDataService {

  private postAuthorInfoSource = new BehaviorSubject<UserResponse | null>(null);
  postAuthorInfo$ = this.postAuthorInfoSource.asObservable();

  setPostAuthorInfo(info: UserResponse) {
    this.postAuthorInfoSource.next(info);
  }}
