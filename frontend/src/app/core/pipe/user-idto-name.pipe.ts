import { Pipe, PipeTransform } from '@angular/core';
import { Observable, map } from 'rxjs';
import { UserService } from '../service/user.service';
import { User } from '../interface/model/user';

@Pipe({
  name: 'userIDToName'
})
export class UserIDToNamePipe implements PipeTransform {
  constructor(private userService: UserService) { }

  transform(id: string): Observable<string | undefined> {
    return this.userService.findByID<User>(id).pipe(
      // map(user => user.fullName)
      map(apiResponse => apiResponse.result?.fullName)
    );


  }

}
