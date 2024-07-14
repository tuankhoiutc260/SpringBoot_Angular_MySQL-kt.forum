import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserIDToNamePipe } from './pipe/user-idto-name.pipe';
import { RoleIdToNamePipe } from './pipe/role-id-to-name.pipe';
import { HtmlToPlaintextPipe } from './pipe/html-to-plaintext.pipe';
import { DataUriToBlobPipe } from './pipe/data-uri-to-blob.pipe';



@NgModule({
  declarations: [
    UserIDToNamePipe,
    RoleIdToNamePipe,
    HtmlToPlaintextPipe,
    DataUriToBlobPipe,

  ],
  imports: [
    CommonModule
  ],
  exports: [
    UserIDToNamePipe,
    RoleIdToNamePipe,
    HtmlToPlaintextPipe,
    DataUriToBlobPipe,

  ]
})
export class CoreModule { }
