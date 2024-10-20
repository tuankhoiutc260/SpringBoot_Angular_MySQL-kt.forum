import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HtmlToPlaintextPipe } from './pipe/html-to-plaintext.pipe';
import { TruncatePipe } from './pipe/truncate.pipe';
import { TimeAgoPipe } from './pipe/time-ago.pipe';
import { UserIdToUserPipe } from './pipe/user-id-to-user.pipe';
import { ToSlugPipe } from './pipe/to-slug.pipe';
import { SubCategoryIdToSubCategoryPipe } from './pipe/sub-category-id-to-sub-category.pipe';
import { CommentIdToCommentPipe } from './pipe/comment-id-to-comment.pipe';
import { ToMinReadPipe } from './pipe/to-min-read.pipe';
import { MessageService } from 'primeng/api';
import { SubCategoryIdToCategoryPipe } from './pipe/sub-category-id-to-category.pipe';

@NgModule({
  declarations: [
    HtmlToPlaintextPipe,
    TruncatePipe,
    TimeAgoPipe,
    UserIdToUserPipe,
    ToSlugPipe,
    SubCategoryIdToSubCategoryPipe,
    SubCategoryIdToCategoryPipe,
    CommentIdToCommentPipe,
    ToMinReadPipe,
  ],
  imports: [
    CommonModule
  ],
  exports: [
    HtmlToPlaintextPipe,
    TruncatePipe,
    TimeAgoPipe,
    UserIdToUserPipe,
    ToSlugPipe,
    SubCategoryIdToSubCategoryPipe,
    SubCategoryIdToCategoryPipe,
    CommentIdToCommentPipe,
    ToMinReadPipe,
  ],
  providers: [MessageService],

})
export class CoreModule { }
