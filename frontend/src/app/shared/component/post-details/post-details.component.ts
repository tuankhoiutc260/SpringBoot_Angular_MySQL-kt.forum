import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';
import { PostRequest } from '../../../core/interface/request/post-request';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss'] // Chỉnh sửa thành styleUrls để chỉ định file CSS
})
export class PostDetailsComponent implements OnInit {
  postID: string = ''; // Khai báo biến postID ở đầu class
  postResponse: PostResponse = {}; // Khai báo biến post ở đầu class

  constructor(
    private postService: PostService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {
    this.postID = this.activatedRoute.snapshot.paramMap.get('postID') || '';
    if (this.postID) {
      this.getPosts(this.postID);
    }
    else {
      console.log("falure")

    }
  }

  getPosts(id: string) {
    this.postService.findByID<PostRequest>(id).subscribe(
      (apiResponse: ApiResponse<PostResponse>) => {
        if (apiResponse.result) {
          this.postResponse = apiResponse.result;
          console.log(apiResponse)
        } else {
          this.postResponse = {};
        }
      },
      (error) => {
        console.error('Error fetching posts', error);
      }
    );
  }
}
