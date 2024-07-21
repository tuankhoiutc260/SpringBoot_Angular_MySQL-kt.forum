import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user.component';
import { FeedComponent } from '../shared/component/page/feed/feed.component';
import { PostDetailsComponent } from '../shared/component/post/post-details/post-details.component';
import { ProfileComponent } from '../shared/component/page/profile/profile.component';

const routes: Routes = [
  {
    path: '',
    component: FeedComponent
  },
  {
    path: ':userName',
    component: ProfileComponent
  },
  {
    path: ':userName/posts/:postID',
    component: PostDetailsComponent
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
