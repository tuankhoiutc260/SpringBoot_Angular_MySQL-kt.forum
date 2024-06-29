import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { PrimengModule } from './primeng.modules';
import { FeedComponent } from './component/feed/feed.component';
import { RouterModule, Routes } from '@angular/router';
import { PostDetailsComponent } from '../shared/component/post-details/post-details.component';

const routes: Routes = [
  {
    path: '',
    component: FeedComponent
  },
  {
    path: 'post', children: [
      {
        path: ':postID',
        component: PostDetailsComponent
      }
    ]
  }
];

@NgModule({
  declarations: [
    UserComponent,
    FeedComponent,
  ],
  imports: [
    CommonModule,
    PrimengModule,
    FormsModule,
    SharedModule,
    RouterModule.forChild(routes),

  ]
})
export class UserModule { }
