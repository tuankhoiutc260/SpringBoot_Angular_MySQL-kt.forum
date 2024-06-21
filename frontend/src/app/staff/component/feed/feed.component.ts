import { Component } from '@angular/core';
import { Post } from '../../../core/interface/post';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PostService } from '../../../core/service/post.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class FeedComponent {

  posts: Post[] = [];
  isVisible: boolean = false
  post: Post = {}
  isEdit: boolean = false;

  constructor(
    private messageService: MessageService,
    private postService: PostService,
    private confirmationService: ConfirmationService,
  ) { }

  ngOnInit(): void {
    this.getAllPosts();
  }

  getAllPosts(): void {
    this.postService.getAllPosts().subscribe({
      next: (posts: Post[]) => {
        this.posts = posts;
      },
      error: (error) => {
        console.log(error)
      }
    })
  }

  openDialogNew(): void {
    this.isEdit = false;
    this.isVisible = true
    this.post = {}
  }

  openDialogEdit(post: Post): void { // Thay đổi kiểu của user thành User
    this.isEdit = true;
    this.isVisible = true;
    this.post = { ...post };
  }

  savePost() {
    if (this.post.id) {
      this.postService.update(this.post.id, this.post).subscribe({
        next: () => {
          this.isVisible = false;
          const index = this.posts.findIndex(post => post.id === this.post.id)
          if (index != -1) {
            this.posts[index] = this.post;
          }
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Post is updated' });
          // window.location.reload();

        },
        error: (error) => {
          console.log(error)
        }
      })
    }
    else {
      this.post.createdBy = '5a1fecce-4d53-46d3-a09a-3ed3c900597b'
      this.postService.create(this.post).subscribe({
        next: (id) => {
          this.post.id = id;
          this.isVisible = false;
          this.posts.push({ ...this.post, id: id, createdDate: new Date().toJSON() });
          this.posts = [...this.posts]
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Post is created' });
          // window.location.reload();

        },
        error: (error) => {
          console.log(error)
        }
      })
    }
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
        if (!this.post.id) return;
        this.postService.deletePost(this.post.id).subscribe({
          next: () => {
            this.posts = this.posts.filter(post => post.id !== this.post.id)
            this.isVisible = false;
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Record deleted' });
          },
          error: (error) => {
            console.log(error)
          }
        })
      },
      reject: () => {
      }
    });
  }
}


