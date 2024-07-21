import { Pipe, PipeTransform } from '@angular/core';
import { Observable, map } from 'rxjs';
import { RoleApiService } from '../../api/service/role-api.service';

@Pipe({
  name: 'roleIdToName'
})
export class RoleIdToNamePipe implements PipeTransform {
  constructor(private roleService: RoleApiService) { }

  transform(id: number): Observable<string | undefined> {
    return this.roleService.findByID(id).pipe(
      map(apiResponse => apiResponse.result?.name)
    );
  }
}
