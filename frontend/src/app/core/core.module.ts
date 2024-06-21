import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserIDToNamePipe } from './pipe/user-idto-name.pipe';



@NgModule({
  declarations: [
    UserIDToNamePipe
  ],
  imports: [
    CommonModule
  ],
  exports:[
    UserIDToNamePipe
  ]
})
export class CoreModule { }
