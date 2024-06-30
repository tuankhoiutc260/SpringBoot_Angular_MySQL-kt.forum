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
  postID: string = '';
  postResponse: PostResponse = {};

  constructor(
    private postService: PostService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {
    this.postID = this.activatedRoute.snapshot.paramMap.get('postID') || '';
    if (this.postID) {
      this.getPost(this.postID);
    }
    else {
      console.log("falure")

    }
  }

  getPost(id: string) {
    this.postService.findByID(id).subscribe({
      next: (apiResponse: ApiResponse<PostResponse>) => {
        if (apiResponse.result) {
          this.postResponse = apiResponse.result;
        } else {
          this.postResponse = { id: '', title: '', content: '' }; // Khởi tạo giá trị mặc định cho PostResponse
        }
      },
      error: (error) => {
        console.error('Error fetching posts', error);
      }
    });
  }
}
