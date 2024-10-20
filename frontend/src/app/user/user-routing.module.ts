import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './page/home-page/home-page.component';
import { PostDetailsPageComponent } from './page/post-details-page/post-details-page.component';
import { PostListPageComponent } from './page/post-list-page/post-list-page.component';
import { ProfileComponent } from './page/profile/profile.component';
import { ProfilePageComponent } from './page/profile-page/profile-page.component';

const routes: Routes = [
  {
    path: '',
    component: HomePageComponent,
  },
  {
    path: 'user/:userId',
    component: ProfilePageComponent
  },
  {
    path: ':categoryTitleSlug/:categoryId',
    component: PostListPageComponent
    // component: CategoryComponent
  },
  {
    path: ':categoryTitleSlug/:categoryId/:subcategoryTitleSlug/:subcategoryId/posts',
    component: PostListPageComponent
  },
  {
    path: ':categoryTitleSlug/:subcategoryTitleSlug/:postTitleSlug/:postId',
    component: PostDetailsPageComponent
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
