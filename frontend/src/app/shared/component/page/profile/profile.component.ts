import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PostResponse } from '../../../../api/interface/response/post-response';
import { UserResponse } from '../../../../api/interface/response/user-response';
import { PostApiService } from '../../../../api/service/post-api.service';
import { UserApiService } from '../../../../api/service/user-api.service';
import { ApiResponse } from '../../../../api/interface/response/apiResponse';
import { AuthService } from '../../../../core/service/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class ProfileComponent implements OnInit {
  totalPosts: number = 0;
  postsResponse: PostResponse[] = [];
  userName: string = ''
  userResponse: UserResponse = {}
  canEdit!: boolean
  private subscription: Subscription = new Subscription();

  constructor(
    private postApiService: PostApiService,
    private userApiService: UserApiService,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.userName = params['userName'];
    });

    this.getUserInfo()
    this.getUserLoginInfo()
  }

  defaultUserLoginInfo: UserResponse = {};
  userLoginInfo: UserResponse | null = null;

  getUserLoginInfo() {
    this.authService.fetchAndSetUserLoginInfo();
    this.subscription.add(
      this.authService.getUserLoginInfo().subscribe(userLoginInfo => {
        this.userLoginInfo = userLoginInfo;
        if (this.userName === this.userLoginInfo?.userName) {
          this.canEdit = true;
        }
      })
    );
  }

  activeButton: string = 'bars';
  toggleActive(button: string) {
    this.activeButton = button;
  }

  getUserInfo() {
    const sub = this.userApiService.findByUserName(this.userName).subscribe({
      next: (apiResponse: ApiResponse<UserResponse>) => {
        const userInfo = apiResponse.result;
        if (userInfo) {
          this.userResponse = userInfo;
          this.getPostsCreatedBy();
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching user information:', error);
      }
    });
    this.subscription.add(sub);
  }

  totalLikes: number = 0
  getPostsCreatedBy(): void {
    const sub = this.postApiService.findByCreatedBy(this.userResponse.id!).subscribe({
      next: (apiResponse: ApiResponse<PostResponse[]>) => {
        const postResponseList = apiResponse.result;
        if (postResponseList) {
          this.postsResponse = postResponseList;
          this.totalPosts = postResponseList.length;
          this.totalLikes = postResponseList.reduce((sum, post) => sum + (post.countLikes || 0), 0);

          if (this.userName === this.userLoginInfo?.userName) {
            this.canEdit = true
          }
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching posts created by: ' + this.userName, error);
      }
    });
    this.subscription.add(sub);
  }

  getPostsLiked(): void {
    const sub = this.postApiService.findPostsLiked(this.userResponse.id!).subscribe({
      next: (apiResponse: ApiResponse<PostResponse[]>) => {
        const postResponseList = apiResponse.result;
        if (postResponseList) {
          this.postsResponse = postResponseList;
          this.canEdit = false
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching posts liked:', error);
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
