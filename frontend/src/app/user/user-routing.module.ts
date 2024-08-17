import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from '../shared/component/page/feed/feed.component';
import { PostDetailsComponent } from '../shared/component/post/post-details/post-details.component';
import { ProfileComponent } from '../shared/component/page/profile/profile.component';
import { PostListComponent } from '../shared/component/post/post-list/post-list.component';

const routes: Routes = [
  {
    path: '',
    component: FeedComponent
  },
  // {
  //   path: ':userName',
  //   component: ProfileComponent
  // },
  {
    path: 'category/:categorySlug/subcategory/:subcategorySlug/:subcategoryId/posts',
    component: PostListComponent
  },
  {
    path: 'category/:categorySlug/subcategory/:subcategorySlug/post/:postSlug/:postId',
    component: PostDetailsComponent
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
