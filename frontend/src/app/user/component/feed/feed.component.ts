import { Component } from '@angular/core';
import { Post } from '../../../core/interface/post';
import { PostService } from '../../../core/service/post.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss'
})
export class FeedComponent {
  posts: Post[] = [];
  isVisible: boolean = false
  post: Post = {}
  isEdit: boolean = false;

  constructor(
    private postService: PostService,
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
}
