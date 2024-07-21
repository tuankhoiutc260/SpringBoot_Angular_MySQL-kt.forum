import { NgModule } from '@angular/core';
import { LoginRoutingModule } from './login-routing.module';
import { AppCommonModule } from '../../app.common.module';
import { LoginComponent } from './login.component';
import { MessageService } from 'primeng/api';

@NgModule({
  declarations: [LoginComponent],
  imports: [
    LoginRoutingModule,
    AppCommonModule
  ],
  providers: [MessageService],
})

export class LoginModule { }
