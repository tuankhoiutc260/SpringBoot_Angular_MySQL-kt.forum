;
import { NotFoundComponent } from './component/not-found/not-found.component'
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostComponent } from './component/post/post.component';
import { CoreModule } from '../core/core.module';
import { PrimengModule } from '../user/primeng.modules';
import { RouterModule } from '@angular/router';
import { PostDetailsComponent } from './component/post-details/post-details.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './component/login/login.component';
import { InteractComponent } from './component/interact/interact.component';
import { HeaderComponent } from './component/header/header.component';
import { FooterComponent } from './component/footer/footer.component';



@NgModule({
  declarations: [
    PostComponent,
    PostDetailsComponent,
    LoginComponent,
    NotFoundComponent,
    InteractComponent,
    HeaderComponent,
    FooterComponent,

  ],
  imports: [
    CommonModule,
    CoreModule,
    PrimengModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule 

  ],
  exports: [
    PostComponent,
    HeaderComponent,
    FooterComponent
  ]
})
export class SharedModule { }
