import { Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';
import { UserApiService } from '../../api/service/rest-api/user-api.service';

@Pipe({
  name: 'userIdToUserName'
})
export class UserIdToUserNamePipe implements PipeTransform {
  constructor(private userService: UserApiService) { }

  transform(id: string): Observable<string | undefined> {
    return this.userService.findById(id).pipe(
      map(apiResponse => apiResponse.result?.userName)
    );
  }
}
