import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
  providers: [MessageService, ConfirmationService]
})
export class FeedComponent implements OnInit {
  postsResponse: PostResponse[] = [];
  isVisible: boolean = false;
  isEdit: boolean = false;
  postResponse: PostResponse = {};
  private subscription!: Subscription;

  constructor(
    private postService: PostService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService

  ) { }

  ngOnInit() {
    this.getAllPosts();
  }

  getAllPosts() {
    this.subscription = this.postService.findAll<PostResponse>().subscribe({
      next: (response: ApiResponse<PostResponse>) => {
        if (Array.isArray(response.result)) {
          this.postsResponse = response.result;
          console.log(this.postsResponse)
        } else {
          this.postsResponse = [];
        }
      },
      error: (error) => {
        console.log('Error fetching posts', error);
      }
    });
  }

  openDialogNew(): void {
    this.isEdit = false;
    this.isVisible = true
    this.postResponse = {}
  }

  openDialogEdit(postResponse: PostResponse): void {
    this.isEdit = true;
    this.isVisible = true;
    this.postResponse = { ...postResponse };
  }

  // ngOnDestroy() {
  //   if (this.subscription) {
  //     this.subscription.unsubscribe();
  //   }
  // }
}
