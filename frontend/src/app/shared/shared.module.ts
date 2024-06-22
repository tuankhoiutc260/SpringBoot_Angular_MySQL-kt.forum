import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostComponent } from './component/post/post.component';
import { CoreModule } from '../core/core.module';
import { PrimengModule } from '../user/primeng.modules';
import { RouterModule } from '@angular/router';
import { PostDetailsComponent } from './component/post-details/post-details.component';
import { LoginComponent } from './component/login/login.component';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    PostComponent,
    PostDetailsComponent,
    LoginComponent,

  ],
  imports: [
    CommonModule,
    CoreModule,
    PrimengModule,
    RouterModule,
    FormsModule

  ],
  exports: [
    PostComponent
  ]
})
export class SharedModule { }
