import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PostRequest } from '../../../core/interface/request/post-request';

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
  postRequest: PostRequest = {};
  private subscription!: Subscription;
  postRequestID: string | null = null; // ID của bài viết, nếu đang cập nhật


  constructor(
    private postService: PostService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService

  ) { }

  ngOnInit() {
    this.getAllPosts();
  }

  getAllPosts() {
    this.postService.findAll().subscribe({
      next: (apiResponse: ApiResponse<PostResponse[]>) => {
        const postResponseList = apiResponse.result;
        if (postResponseList) {
          this.postsResponse = postResponseList;
        } else {
          console.error('No result found in response: ', apiResponse.message);
        }
      },
      error: (apiResponse: ApiResponse<PostResponse>) => {
        console.error(apiResponse.message)
      }
    });
  }

  openDialogNew() {
    this.isEdit = false;
    this.isVisible = true
    this.postResponse = {}
  }

  openDialogEdit(postResponse: PostResponse) {
    this.isEdit = true;
    this.isVisible = true;
    this.postResponse = { ...postResponse };
  }

  savePost() {
    this.postRequest = { ...this.postResponse }
    this.postRequestID = this.postResponse.id ?? null;
    this.postService.save(this.postRequestID, this.postRequest).subscribe({
      next: (apiResponse: ApiResponse<PostResponse>) => {
        const postResponse = apiResponse.result;
        if (postResponse) {
          if (this.postRequestID) {
            // Nếu đang cập nhật, thay thế bài viết trong danh sách
            const index = this.postsResponse.findIndex(post => post.id === this.postRequestID);
            if (index !== -1) {
              this.postsResponse[index] = postResponse;
            }
            this.showMessage('info', 'Confirmed', 'Post updated')

          } else {
            this.postsResponse.unshift(postResponse);
            this.showMessage('info', 'Confirmed', 'Post created')
          }
          this.isVisible = false
          this.resetForm();
          this.loadPage()
        } else {
          console.error('No result found in response:', apiResponse);
        }
      },
      error: (apiResponse: ApiResponse<PostResponse>) => {
        this.showMessage('error', 'Error', apiResponse.message ?? '');
      }
    });
  }

  resetForm() {
    this.postRequest = {};
    this.postRequestID = null;
  }

  loadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
    this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  }

  deletePost(event: Event) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to delete this post?',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-danger p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "none",
      rejectIcon: "none",

      accept: () => {
        if (!this.postResponse.id) return;

        this.postService.delete(this.postResponse.id).subscribe({
          next: () => {
            this.postsResponse = this.postsResponse.filter(postResponse => postResponse.id !== this.postResponse.id);
            this.isVisible = false;
            this.showMessage('info', 'Confirmed', 'Post deleted')
            this.loadPage()
          },
          error: (error) => {
            console.log(error);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete post' });
          }
        });
      },
      reject: () => {
        // this.showMessage('error', 'Rejected', 'You have rejected')
      }
    });
  }



}
