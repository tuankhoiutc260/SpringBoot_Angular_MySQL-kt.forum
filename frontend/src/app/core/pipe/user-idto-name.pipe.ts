import { Pipe, PipeTransform } from '@angular/core';
import { Observable, map } from 'rxjs';
import { User } from '../interface/user';
import { UserService } from '../service/user.service';

@Pipe({
  name: 'userIDToName'
})
export class UserIDToNamePipe implements PipeTransform {
  constructor(private userService: UserService){}

  transform(id: string): Observable<string | undefined>{
    return this.userService.getUser(id).pipe(
      map(user => user.fullName)
    );
      
 
  }

}
