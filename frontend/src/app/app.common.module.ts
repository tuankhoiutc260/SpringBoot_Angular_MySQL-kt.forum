import {ModuleWithProviders, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import { PrimengModule } from './app.primeng.modules';
import { CoreModule } from "./core/core.module";
import { HeaderComponent } from './shared/component/layout/header/header.component';
import { FooterComponent } from './shared/component/layout/footer/footer.component';
import { PostListComponent } from './shared/component/post/post-list/post-list.component';
import { PostComponent } from './shared/component/post/post/post.component';
import { FeedComponent } from './shared/component/page/feed/feed.component';
import { PostDetailsComponent } from './shared/component/post/post-details/post-details.component';
import { InteractComponent } from './shared/component/post/interact/interact.component';
import { ProfileComponent } from './shared/component/page/profile/profile.component';
import { CreateUpdatePostComponent } from './shared/component/post/create-update-post/create-update-post.component';
import { CommentsComponent } from './shared/component/post/comment/comment.component';


@NgModule({
    imports: [
    CommonModule,
    ReactiveFormsModule,
    PrimengModule,
    CoreModule,
],
    exports: [
        PrimengModule,
        FormsModule,
        
        ReactiveFormsModule,
        CommonModule,

        HeaderComponent,
        FooterComponent,

        ProfileComponent,

        FeedComponent,
        PostListComponent,
        PostComponent,
        PostDetailsComponent,
        InteractComponent,
        CreateUpdatePostComponent,
        CommentsComponent,
    ],
    declarations: [
        // layout
        HeaderComponent,
        FooterComponent,

        // page
        ProfileComponent,

        // post
        FeedComponent,
        PostListComponent,
        PostComponent,
        PostDetailsComponent,
        InteractComponent,
        CreateUpdatePostComponent,
        CommentsComponent,
    ]
})

export class AppCommonModule {
    static forRoot(): ModuleWithProviders<any> {
        return {
            ngModule: AppCommonModule,
            providers: [],
        };
    }
}