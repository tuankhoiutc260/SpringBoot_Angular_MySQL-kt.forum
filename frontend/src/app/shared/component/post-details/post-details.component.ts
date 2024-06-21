import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../../core/service/post.service';
import { Post } from '../../../core/interface/post';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrl: './post-details.component.scss'
})
export class PostDetailsComponent {
  postID: string = ''
  post: Post = {}
  constructor(
    private postService: PostService,
    private activatedRoute: ActivatedRoute
  ) {

  }
  ngOnInit() {
    this.postID = this.activatedRoute.snapshot.paramMap.get('postID') || '';
    this.getPost(this.postID)
  }

  getPost(id: string) {
    this.postService.getPost(id).subscribe({
      next: (post) => {
        this.post = post;
      },
      error: (error) => {
        console.log(error)
      }
    })
  }
}
