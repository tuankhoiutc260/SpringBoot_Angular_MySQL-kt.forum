import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostComponent } from './component/post/post.component';
import { CoreModule } from '../core/core.module';
import { PrimengModule } from '../user/primeng.modules';
import { RouterModule } from '@angular/router';
import { PostDetailsComponent } from './component/post-details/post-details.component';



@NgModule({
  declarations: [
    PostComponent,
    PostDetailsComponent,

  ],
  imports: [
    CommonModule,
    CoreModule,
    PrimengModule,
    RouterModule

  ],
  exports: [
    PostComponent
  ]
})
export class SharedModule { }
