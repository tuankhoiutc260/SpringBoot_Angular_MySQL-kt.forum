import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StaffComponent } from './staff.component';
import { PrimengModule } from '../user/primeng.modules';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from './component/feed/feed.component';
import { UserManagementComponent } from './component/user-management/user-management.component';
import { FormsModule } from '@angular/forms';
import { UserIDToNamePipe } from '../core/pipe/user-idto-name.pipe';
import { CoreModule } from '../core/core.module';
import { PostComponent } from '../shared/component/post/post.component';
import { SharedModule } from '../shared/shared.module';
import { PostDetailsComponent } from '../shared/component/post-details/post-details.component';

const routes: Routes = [
  {
    path: '',
    component: FeedComponent
  },
  {
    path: 'user-management',
    component: UserManagementComponent
  },
  {
    path: 'posts', children: [
      {
        path: ':postID',
        component: PostDetailsComponent
      }
    ]
  }
];

@NgModule({
  declarations: [
    StaffComponent,
    FeedComponent,
    UserManagementComponent,


  ],
  imports: [
    CommonModule,
    PrimengModule,
    FormsModule,
    RouterModule.forChild(routes),
    SharedModule
  ]
})
export class StaffModule { }
