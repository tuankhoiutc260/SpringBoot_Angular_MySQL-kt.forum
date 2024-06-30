import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit, OnDestroy {
  postsResponse: PostResponse[] = [];
  isVisible: boolean = false;
  isEdit: boolean = false;
  postResponse: PostResponse = {};
  private subscription!: Subscription;
  private socketSubscription!: Subscription;

  constructor(
    private postService: PostService,
  ) { }

  ngOnInit() {
    this.getAllPosts();

    // Ví dụ: Đăng ký theo dõi sự kiện từ socket để nhận bài đăng mới
    // this.socketSubscription = this.socketService.onNewPost().subscribe((newPost: PostResponse) => {
    //   // Thêm bài đăng mới vào đầu mảng hoặc xử lý theo nhu cầu
    //   this.postsResponse.unshift(newPost);
    // });
  }


  getAllPosts(): void {
    this.postService.findAll().subscribe({
      next: (apiResponse: ApiResponse<PostResponse[]>) => {
        const postResponseList = apiResponse.result;
        if (postResponseList) {
          this.postsResponse = postResponseList;
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching posts:', error);
      }
    });
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.socketSubscription) {
      this.socketSubscription.unsubscribe();
    }
  }
}
