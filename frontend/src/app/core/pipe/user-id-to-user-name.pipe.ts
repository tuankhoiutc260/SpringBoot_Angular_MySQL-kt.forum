import { Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';
import { UserApiService } from '../../api/service/rest-api/user-api.service';

@Pipe({
  name: 'userIdToUserName'
})
export class UserIdToUserNamePipe implements PipeTransform {
  constructor(private userApiService: UserApiService) { }

  transform(userId: string): Observable<string | undefined> {
    return this.userApiService.getById(userId).pipe(
      map(userResponse => userResponse.userName)
    );
  }
}
