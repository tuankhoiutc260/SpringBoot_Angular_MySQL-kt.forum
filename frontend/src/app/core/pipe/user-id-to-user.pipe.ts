import { Pipe, PipeTransform } from '@angular/core';
import { Observable, map } from 'rxjs';
import { UserResponse } from '../../api/model/response/user-response';
import { UserApiService } from '../../api/service/rest-api/user-api.service';

@Pipe({
  name: 'userIdToUser'
})
export class UserIdToUserPipe implements PipeTransform {

  constructor(private userApiService: UserApiService) { }

  transform(userId: string): Observable<UserResponse | undefined> {
    return this.userApiService.getById(userId).pipe(
      map(userResponse => userResponse)
    );
  }
}
