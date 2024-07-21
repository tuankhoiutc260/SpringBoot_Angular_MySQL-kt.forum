import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserApiService } from '../../../../api/service/user-api.service';
import { Router } from '@angular/router';
import { SharedDataService } from '../../../../core/service/shared-data.service';
import { ApiResponse } from '../../../../api/model/response/apiResponse';
import { PostResponse } from '../../../../api/model/response/post-response';
import { UserResponse } from '../../../../api/model/response/user-response';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent implements OnInit, OnDestroy {
  @Input() postResponse!: PostResponse;
  @Input() userLoginInfo!: UserResponse
  @Input() canEdit!: boolean;
  @Output() isDelete = new EventEmitter<string>();
  @Output() isUpdate = new EventEmitter();

  deletePost() {
    this.isDelete.emit(this.postResponse.id);
  }

  minRead: number = 0
  postAuthorInfo: UserResponse = {}

  private subscription: Subscription = new Subscription();

  constructor(
    private userApiService: UserApiService,
    private router: Router,
    private sharedDataService: SharedDataService
  ) { }

  ngOnInit(): void {
    this.minRead = this.calculateMinRead(this.postResponse.content!)
    this.onGetPostAuthor();
  }

  calculateMinRead(text: string): number {
    const wordsPerMinute = 200;
    const cleanText = text.replace(/[^\w\s]/gi, '');
    const textLength = cleanText.split(/\s+/).length;
    return Math.ceil(textLength / wordsPerMinute);
  }

  onGetPostAuthor() {
    const sub = this.userApiService.findByID(this.postResponse.createdBy!).subscribe({
      next: (apiResponse: ApiResponse<UserResponse>) => {
        const postAuthorInfo = apiResponse.result;
        if (postAuthorInfo) {
          this.postAuthorInfo = apiResponse.result!
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching Post Author Info:', error);
      }
    });
    this.subscription.add(sub);
  }
  
  navigateToPost(userName: string, postID: string): void {
    if (this.postAuthorInfo && this.postAuthorInfo.userName) {
      this.sharedDataService.setPostAuthorInfo(this.postAuthorInfo);
      const routeUrl = userName === this.userLoginInfo.userName
        ? `${this.userLoginInfo.userName}/posts/${postID}`
        : `${userName}/posts/${postID}`;

      this.router.navigate([routeUrl]);
    } else {
      console.error('postAuthor is not available');
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
