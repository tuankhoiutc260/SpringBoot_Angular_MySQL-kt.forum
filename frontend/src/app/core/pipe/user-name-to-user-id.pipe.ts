import { Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';
import { UserApiService } from '../../api/service/rest-api/user-api.service';

@Pipe({
  name: 'userNameToUserId'
})
export class UserNameToUserIdPipe implements PipeTransform {

  constructor(private userApiService: UserApiService) { }

  transform(userName: string): Observable<string | undefined> {
    return this.userApiService.getByUserName(userName).pipe(
      map(userResponse => userResponse.id)
    );
  }
}
