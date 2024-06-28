import { Component } from '@angular/core';
import { Post } from '../../../core/interface/model/post';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostRequest } from '../../../core/interface/request/post-request';
import { PostResponse } from '../../../core/interface/response/post-response';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class FeedComponent {
  posts: PostResponse[] = [];
  isVisible: boolean = false
  post: PostResponse = {}
  isEdit: boolean = false;

  constructor(
    private messageService: MessageService,
    private postService: PostService,
    private confirmationService: ConfirmationService,
  ) { }

  ngOnInit(): void {
    this.getAllPosts();
  }

  getAllPosts() {
    this.postService.findAll<PostResponse>().subscribe(
      (apiResponse: ApiResponse<PostResponse>) => {
        if (Array.isArray(apiResponse.result)) {
          this.posts = apiResponse.result;
        } else {
          this.posts = [];
        }
      },
      (error) => {
        console.error('Error fetching posts', error);
      }
    );
  }

  openDialogNew(): void {
    this.isEdit = false;
    this.isVisible = true
    this.post = {}
  }

  openDialogEdit(post: PostResponse): void {
    this.isEdit = true;
    this.isVisible = true;
    this.post = { ...post };
  }

  savePost() {
    if (this.post.id) {
      this.postService.update<Post>(this.post.id, this.post).subscribe({
        next: () => {
          this.isVisible = false;
          const index = this.posts.findIndex(post => post.id === this.post.id);
          if (index !== -1) {
            this.posts[index] = { ...this.posts[index], ...this.post };
          }
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Post is updated' });
        },
        error: (error) => {
          console.log('Error updating post:', error);
        }
      });
    } else {
      this.postService.create<Post>(this.post).subscribe({
        next: (apiResponse: ApiResponse<Post>) => {
          // this.post.id = id;
          var id = apiResponse.result?.id
          this.isVisible = false;
          const newPost = {
            ...this.post, id
            // , createdDate: new Date().toJSON() 
          };
          this.posts.push(newPost);
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Post is created' });
        },
        error: (error) => {
          console.log('Error creating post:', error);
          // Handle error, show error message to the user
        }
      });
    }
  }
}

// deletePost(event: Event) {
//   this.confirmationService.confirm({
//     target: event.target as EventTarget,
//     message: 'Do you want to delete this post?',
//     header: 'Delete Confirmation',
//     icon: 'pi pi-info-circle',
//     acceptButtonStyleClass: "p-button-danger p-button-text",
//     rejectButtonStyleClass: "p-button-text p-button-text",
//     acceptIcon: "none",
//     rejectIcon: "none",

//     accept: () => {
//       if (!this.post.id) return;
//       this.postService.delete<Post>(this.post.id).subscribe({
//         next: () => {
//           this.posts = this.posts.filter(post => post.id !== this.post.id)
//           this.isVisible = false;
//           this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Record deleted' });
//         },
//         error: (error) => {
//           console.log(error)
//         }
//       })
//     },
//     reject: () => {
//     }
//   });
// }




