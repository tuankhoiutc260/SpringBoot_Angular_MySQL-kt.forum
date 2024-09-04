import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { catchError, Subject, takeUntil, tap, throwError } from 'rxjs';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { MessageService } from 'primeng/api';
import { PostRequest } from '../../../../api/model/request/post-request';
import { PostResponse } from '../../../../api/model/response/post-response';

@Component({
  selector: 'app-create-update-post',
  templateUrl: './create-update-post.component.html',
  styleUrl: './create-update-post.component.scss',
  providers: [MessageService]
})
export class CreateUpdatePostComponent implements OnDestroy {
  @Input() isDialogVisible!: boolean
  @Input() postResponse: PostResponse | undefined;
  @Output() isDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Input() subCategoryId!: string

  private destroy$ = new Subject<void>();

  postContent?: {
    html: string | undefined,
    text: string | undefined,
  }

  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  isTitleValid = true;
  isContentValid = true;

  postRequest: PostRequest = {
    title: '',
    content: '',
    subCategoryId: ''
  }

  validateFields() {
    this.isTitleValid = this.postRequest.title !== undefined && this.postRequest.title.trim() !== '';
    this.isContentValid = typeof (this.postContent) == 'object'
      ? !(this.postContent?.html === undefined && this.postContent?.text === undefined)
      : this.postContent !== null
  }

  getHtmlContent(): string {
    const htmlContent = typeof this.postContent === 'object'
      && this.postContent !== null
      && 'html' in this.postContent
      ? this.postContent.html
      : this.postContent as unknown as string;
    return htmlContent!
  }

  onSubmit() {
    if (this.postResponse?.id) {
      this.onUpdatePost()
    }
    else {
      this.onCreatePost()
    }
  }

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  onCreatePost() {
    this.validateFields()
    this.postRequest.content = this.getHtmlContent();
    this.postRequest.subCategoryId = this.subCategoryId;

    this.postApiService.create(this.postRequest)
      .pipe(
        tap(() => {
          this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Post created' });
          this.isDialogVisible = false;
          this.onLoadPage()
        }),
        takeUntil(this.destroy$),
        catchError(error => {
          console.error('Error creating post:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error creating post' });
          return throwError(() => new Error(error.message || 'Server error'));
        })
      ).subscribe()
  }

  onUpdatePost() {
    this.validateFields();
    this.postRequest.content = this.getHtmlContent();
    
    if (this.postResponse?.id) { 
      this.postApiService.update(this.postResponse.id, this.postRequest)
        .pipe(
          tap(() => {
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Post updated' });
            this.isDialogVisible = false;
            this.onLoadPage();
          }),
          takeUntil(this.destroy$),
          catchError(error => {
            console.error('Error during update:', error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error updating post' });
            return throwError(() => new Error(error.message || 'Server error'));
          })
        ).subscribe();
    } else {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Post ID is missing' });
    }
  }
  
  onDialogHide() {
    this.isDialogVisible = false;
    this.isDialogVisibleChange.emit(false);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
