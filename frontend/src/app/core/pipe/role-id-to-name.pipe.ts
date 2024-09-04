import { Pipe, PipeTransform } from '@angular/core';
import { Observable, map } from 'rxjs';
import { RoleApiService } from '../../api/service/rest-api/role-api.service';

@Pipe({
  name: 'roleIdToName'
})
export class RoleIdToNamePipe implements PipeTransform {
  constructor(private roleApiService: RoleApiService) { }

  transform(roleId: number): Observable<string | undefined> {
    return this.roleApiService.getById(roleId).pipe(
      map(roleResponse => roleResponse.name)
    );
  }
}
