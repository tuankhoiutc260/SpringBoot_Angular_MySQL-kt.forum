import { Pipe, PipeTransform } from '@angular/core';
import { RoleService } from '../service/role.service';
import { Observable, map } from 'rxjs';

@Pipe({
  name: 'roleIdToName'
})
export class RoleIdToNamePipe implements PipeTransform {
  constructor(private roleService: RoleService) { }

  transform(id: number): Observable<string | undefined> {
    return this.roleService.findByID(id).pipe(
      map(apiResponse => apiResponse.result?.name)
    );
  }
}
