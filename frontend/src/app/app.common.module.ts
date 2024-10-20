import { ModuleWithProviders, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PrimengModule } from './app.primeng.modules';
import { CoreModule } from "./core/core.module";
import { LazyLoadImageModule } from 'ng-lazyload-image';
import { HeaderComponent } from './shared/component/layout/header/header.component';
import { FooterComponent } from './shared/component/layout/footer/footer.component';
import { SubCategoryComponent } from './user/component/sub-category/sub-category/sub-category.component';
import { MostRecentlyCreatedPostsComponent } from './user/component/ranking/most-recently-created-posts/most-recently-created-posts.component';
import { SubCategoryRankComponent } from './user/component/ranking/sub-category-rank/sub-category-rank.component';
import { TopTrendingPostsComponent } from './user/component/ranking/top-trending-posts/top-trending-posts.component';
import { UserRankComponent } from './user/component/ranking/user-rank/user-rank.component';
import { CreateUpdateSubCategoryComponent } from './user/component/sub-category/create-update-sub-category/create-update-sub-category.component';
import { CategoryComponent } from './user/component/category/category/category.component';
import { CommentFormComponent } from './user/component/comment/comment-form/comment-form.component';
import { CommentComponent } from './user/component/comment/comment/comment.component';
import { CreateUpdatePostComponent } from './user/component/post/create-update-post/create-update-post.component';
import { PostDetailsComponent } from './user/component/post/post-details/post-details.component';
import { PostListComponent } from './user/component/post/post-list/post-list.component';
import { PostComponent } from './user/component/post/post/post.component';
import { NavbarComponent } from './admin/component/navbar/navbar.component';
import { UserComponent } from './admin/component/user/user.component';
import { CreateUpdateUserComponent } from './shared/component/features/create-update-user/create-update-user.component';
import { HomeComponent } from './user/component/home/home.component';
import { FeedComponent } from './user/component/feed/feed.component';
import { HeroSectionComponent } from './user/section/hero-section/hero-section.component';
import { BodySectionComponent } from './user/section/body-section/body-section.component';
import { HomePageComponent } from './user/page/home-page/home-page.component';
import { PostListPageComponent } from './user/page/post-list-page/post-list-page.component';
import { PostDetailsPageComponent } from './user/page/post-details-page/post-details-page.component';
import { PostV2Component } from './user/component/post/post-v2/post-v2.component';
import { RandomPostListComponent } from './user/component/post/random-post-list/random-post-list.component';
import { CategoryListHomeComponent } from './user/component/category/category-list-home/category-list-home.component';
import { LoginDialogComponent } from './shared/component/features/login-dialog/login-dialog.component';
import { ProfilePageComponent } from './user/page/profile-page/profile-page.component';
import { ProfileComponent } from './user/component/profile/profile/profile.component';
import { PostV3Component } from './user/component/post/post-v3/post-v3.component';
import { ChangePasswordDialogComponent } from './user/component/profile/change-password-dialog/change-password-dialog.component';
import { EditProfileDialogComponent } from './user/component/profile/edit-profile-dialog/edit-profile-dialog.component';

@NgModule({
    imports: [
        CommonModule,
        ReactiveFormsModule,
        PrimengModule,
        CoreModule,
        LazyLoadImageModule,
    ],
    exports: [
        // FEATURE
        LoginDialogComponent,

        // PAGE
        HomePageComponent,
        PostListPageComponent,
        PostDetailsPageComponent,
        ProfilePageComponent,

        // PROFILE
        ProfileComponent,
        ChangePasswordDialogComponent,
        EditProfileDialogComponent,

        // CATEGORY
        CategoryComponent,  
        CategoryListHomeComponent,

        // COMMENT
        CommentComponent,
        CommentFormComponent,

        // POST
        PostComponent,
        PostV2Component,
        PostV3Component,
        PostDetailsComponent,
        PostListComponent,
        RandomPostListComponent,

        // RANKING
        MostRecentlyCreatedPostsComponent,
        SubCategoryRankComponent,
        TopTrendingPostsComponent,
        UserRankComponent,

        // SECTION
        HeroSectionComponent,
        BodySectionComponent,

        // LAYOUT
        HeaderComponent,
        FooterComponent,




        PrimengModule,
        FormsModule,

        ReactiveFormsModule,
        CommonModule,



        HomeComponent,
        FeedComponent,

        CreateUpdatePostComponent,



        SubCategoryComponent,
        CreateUpdateSubCategoryComponent,




        // ADMIN
        NavbarComponent,
        UserComponent,


        // SHARED
        CreateUpdateUserComponent,


    ],
    declarations: [
        // FEATURE
        LoginDialogComponent,

        // PAGE
        HomePageComponent,
        PostListPageComponent,
        PostDetailsPageComponent,
        ProfilePageComponent,

        // PROFILE
        ProfileComponent,
        ChangePasswordDialogComponent,
        EditProfileDialogComponent,

        // CATEGORY
        CategoryComponent,
        CategoryListHomeComponent,

        // COMMENT
        CommentComponent,
        CommentFormComponent,

        // POST
        PostComponent,
        PostV2Component,
        PostV3Component,
        PostDetailsComponent,
        PostListComponent,
        RandomPostListComponent,

        // RANKING
        MostRecentlyCreatedPostsComponent,
        SubCategoryRankComponent,
        TopTrendingPostsComponent,
        UserRankComponent,

        // SECTION
        HeroSectionComponent,
        BodySectionComponent,

        // LAYOUT
        HeaderComponent,
        FooterComponent,



        // post
        HomeComponent,
        FeedComponent,

        CreateUpdatePostComponent,


        SubCategoryComponent,
        CreateUpdateSubCategoryComponent,

        //ADMIN
        NavbarComponent,
        UserComponent,

        //SHARED
        CreateUpdateUserComponent,

    ]
})

export class AppCommonModule {
    static forRoot(): ModuleWithProviders<AppCommonModule> {
        return {
            ngModule: AppCommonModule,
            providers: [],
        };
    }
}