import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  postsResponse: PostResponse[] = [];
  isVisible: boolean = false;
  isEdit: boolean = false;
  postResponse: PostResponse = {};

  constructor(private postService: PostService) { }

  ngOnInit() {
    this.getAllPosts();
  }

  getAllPosts() {
    this.postService.findAll<PostResponse>().subscribe(
      (response: ApiResponse<PostResponse>) => {
        if (Array.isArray(response.result)) {
          this.postsResponse = response.result;
          console.log(this.postsResponse)
        } else {
          this.postsResponse = [];
        }
      },
      (error) => {
        console.log('Error fetching posts', error);
      }
    );
  }
}
