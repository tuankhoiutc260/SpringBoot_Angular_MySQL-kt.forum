import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { FeedComponent } from './component/feed/feed.component';



@NgModule({
  declarations: [
    UserComponent,
    FeedComponent
  ],
  imports: [
    CommonModule
  ]
})
export class UserModule { }
