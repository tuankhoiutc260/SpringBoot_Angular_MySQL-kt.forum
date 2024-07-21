import { Component, Input, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserResponse } from '../../../../api/interface/response/user-response';
import { PostResponse } from '../../../../api/interface/response/post-response';
import { PostApiService } from '../../../../api/service/post-api.service';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class PostListComponent implements OnInit, OnDestroy {
  @Input() postsResponse: PostResponse[] = []
  @Input() userName: string = ''
  @Input() canEdit!: boolean
  @Input() defaultUserLoginInfo: UserResponse = { };
  @Input() userLoginInfo: UserResponse | null = null;

  totalPosts: number = 0;
  pageSize: number = 12;
  pagedPosts: PostResponse[] = [];
  private subscription: Subscription = new Subscription();

  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) { }
  
  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['postsResponse'] && !changes['postsResponse'].firstChange) {
      this.totalPosts = this.postsResponse.length
      this.updatePagedPosts(0);
    }
  }

  updatePagedPosts(startIndex: number): void {
    this.pagedPosts = this.postsResponse.slice(startIndex, startIndex + this.pageSize);
  }

  onPageChange(event: any): void {
    const firstItemIndex = event.first;
    this.updatePagedPosts(firstItemIndex);
  }

  // Open dialog update Post
  isDialogVisible: boolean = false;
  selectedPostResponse: PostResponse = {}; 

  openDialogEdit(postResponse: PostResponse): void {
    this.selectedPostResponse = postResponse;
    this.isDialogVisible = true;
  }
  // 

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
    this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  }

  onDeletePost(postId: string) {
    this.confirmationService.confirm({
      target: undefined,
      message: 'Do you want to delete this post?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",
      accept: () => {
        const sub = this.postApiService.delete(postId).subscribe({
          next: () => {
            this.postsResponse = this.postsResponse.filter(postResponse => postResponse.id !== postId);
            this.isDialogVisible = false;
            this.showMessage('info', 'Confirmed', 'Post deleted');
            this.onLoadPage();
          },
          error: (error) => {
            console.log(error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete post' });
          }
        });
        this.subscription.add(sub);
      },
      reject: () => {
        // this.showMessage('error', 'Rejected', 'You have rejected')
      }
    });
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
