import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from '../shared/component/page/feed/feed.component';
import { PostDetailsComponent } from '../shared/component/post/post-details/post-details.component';
import { PostListComponent } from '../shared/component/post/post-list/post-list.component';
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
    path: 'category/:categoryTitleSlug/subcategory/:subcategoryTitleSlug/:subcategoryId/posts',
    component: PostListComponent
  },
  {
    path: 'category/:categoryTitleSlug/subcategory/:subcategoryTitleSlug/post/:postTitleSlug/:postId',
    component: PostDetailsComponent
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
