import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserIDToNamePipe } from './pipe/user-idto-name.pipe';
import { RoleIdToNamePipe } from './pipe/role-id-to-name.pipe';



@NgModule({
  declarations: [
    UserIDToNamePipe,
    RoleIdToNamePipe
  ],
  imports: [
    CommonModule
  ],
  exports: [
    UserIDToNamePipe,
    RoleIdToNamePipe

  ]
})
export class CoreModule { }
