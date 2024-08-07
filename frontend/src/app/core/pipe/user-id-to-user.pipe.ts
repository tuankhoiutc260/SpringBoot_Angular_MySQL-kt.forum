import { Pipe, PipeTransform } from '@angular/core';
import { Observable, map } from 'rxjs';
import { UserApiService } from '../../api/service/rest-api/user-api.service';
import { UserResponse } from '../../api/model/response/user-response';

@Pipe({
  name: 'userIdToUser'
})
export class UserIdToUserPipe implements PipeTransform {

  constructor(private userService: UserApiService) { }

  transform(id: string): Observable<UserResponse | undefined> {
    return this.userService.findById(id).pipe(
      map(apiResponse => apiResponse.result)
    );
  }
}
