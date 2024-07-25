import { NgModule } from '@angular/core';

import { UserRoutingModule } from './user-routing.module';
import { AppCommonModule } from '../app.common.module';
import { UserComponent } from './user.component';


@NgModule({
  declarations: [UserComponent],
  imports: [
    UserRoutingModule,
    AppCommonModule
  ]
})
export class UserModule { }
