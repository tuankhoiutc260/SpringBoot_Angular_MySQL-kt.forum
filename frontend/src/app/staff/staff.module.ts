import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StaffComponent } from './staff.component';
import { FeedComponent } from './component/feed/feed.component';
import { UserManagementComponent } from './component/user-management/user-management.component';
import { RouterModule, Routes } from '@angular/router';
import { PrimengModule } from '../user/primeng.modules';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { PostDetailsComponent } from '../shared/component/post-details/post-details.component';
import { RoleIdToNamePipe } from '../core/pipe/role-id-to-name.pipe';
import { CoreModule } from '../core/core.module';
// import { SharedModule } from 'primeng/api';

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
    StaffComponent,
    FeedComponent,
    UserManagementComponent,
  ],
  imports: [
    CoreModule,
    CommonModule,
    PrimengModule,
    FormsModule,
    SharedModule,
    RouterModule.forChild(routes),
  ]
})
export class StaffModule { }
