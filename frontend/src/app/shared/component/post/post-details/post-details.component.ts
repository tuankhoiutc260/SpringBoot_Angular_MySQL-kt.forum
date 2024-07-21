import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { UserResponse } from '../../../../api/interface/response/user-response';
import { PostResponse } from '../../../../api/interface/response/post-response';
import { Subscription } from 'rxjs';
import { PostApiService } from '../../../../api/service/post-api.service';
import { AuthService } from '../../../../core/service/auth.service';
import { ApiResponse } from '../../../../api/interface/response/apiResponse';
import { SharedDataService } from '../../../../core/service/shared-data.service';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss'] ,
  providers: [MessageService]
})
export class PostDetailsComponent implements OnInit, OnDestroy {
  postID: string = '';
  userName: string = ''
  postResponse: PostResponse = {};
  postAuthorInfo: UserResponse | null = null;
  private subscription: Subscription = new Subscription();

  constructor(
    private postApiService: PostApiService,
    private authService: AuthService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private sharedDataService: SharedDataService
  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.userName = params['userName'];
      this.postID = params['postID'];
      this.getPostDetails(this.postID);
    });

    this.sharedDataService.postAuthorInfo$.subscribe(info => {
      this.postAuthorInfo = info;
    });
  }
  
  onLikeFailed() {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: 'You should Sign in first' });
  }
  
  isLogin(): boolean {
    return this.authService.getCurrentUserName() ? true : false
  }

  getPostDetails(id: string) {
    const sub = this.postApiService.findByID(id).subscribe({
      next: (apiResponse: ApiResponse<PostResponse>) => {
        const post = apiResponse.result;
        if(post){
          this.postResponse = post
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
