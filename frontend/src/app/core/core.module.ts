import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserIDToNamePipe } from './pipe/user-idto-name.pipe';
import { RoleIdToNamePipe } from './pipe/role-id-to-name.pipe';
import { ImageExtensionFilterPipe } from './pipe/image-extension-filter.pipe';



@NgModule({
  declarations: [
    UserIDToNamePipe,
    RoleIdToNamePipe,
    ImageExtensionFilterPipe,

  ],
  imports: [
    CommonModule
  ],
  exports: [
    UserIDToNamePipe,
    RoleIdToNamePipe,
    ImageExtensionFilterPipe

  ]
})
export class CoreModule { }
