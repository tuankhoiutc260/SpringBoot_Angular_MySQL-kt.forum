import { Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';
import { UserApiService } from '../../api/service/user-api.service';

@Pipe({
  name: 'userNameToUserId'
})
export class UserNameToUserIdPipe implements PipeTransform {

  constructor(private userService: UserApiService) { }

  transform(userName: string): Observable<string | undefined> {
    return this.userService.findByUserName(userName).pipe(
      map(apiResponse => apiResponse.result?.id)
    );
  }
}
