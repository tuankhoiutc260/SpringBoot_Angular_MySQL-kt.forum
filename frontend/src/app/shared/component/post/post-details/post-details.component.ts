import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { AuthService } from '../../../../core/service/auth.service';
import { SharedDataService } from '../../../../core/service/shared-data.service';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { PostResponse } from '../../../../api/model/response/post-response';
import { UserResponse } from '../../../../api/model/response/user-response';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss'],
  providers: [MessageService]
})
export class PostDetailsComponent implements OnInit, OnDestroy {
  postId: string = '';
  userName: string = '';
  postResponse: PostResponse = {
    id: '',
    createdBy: '',
    title: '',
    content: '',
    tags: [],
    createdDate: '',
    lastModifiedDate: '',
    lastModifiedBy: ''
  };
  postAuthorInfo: UserResponse | null = null;
  private subscription: Subscription = new Subscription();
  safeContent!: SafeHtml;


  constructor(
    private postApiService: PostApiService,
    private authService: AuthService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private sharedDataService: SharedDataService,
    private sanitizer: DomSanitizer

  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.userName = params['userName'];
      this.postId = params['postId'];
      this.getPostDetails(this.postId);
    });

    this.sharedDataService.postAuthorInfo$.subscribe(info => {
      this.postAuthorInfo = info;
    });
  }

  onLikeFailed() {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: 'You should Sign in first' });
  }

  isLogin(): boolean {
    return this.authService.getCurrentUserName() ? true : false;
  }

  getPostDetails(id: string) {
    const sub = this.postApiService.findById(id).subscribe({
      next: (apiResponse: ApiResponse<PostResponse>) => {
        const post = apiResponse.result;
        if (post) {
          this.postResponse = post;
          this.safeContent = this.sanitizer.bypassSecurityTrustHtml(post.content);
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching Post:', error);
      }
    });
    this.subscription.add(sub);
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
