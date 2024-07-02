import { Component, Input } from '@angular/core';
import { LikeService } from '../../../core/service/like.service';
import { LikeRequest } from '../../../core/interface/request/like-request';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { LikeResponse } from '../../../core/interface/response/likeResponse';

@Component({
  selector: 'app-interact',
  templateUrl: './interact.component.html',
  styleUrl: './interact.component.scss'
})
export class InteractComponent {
  @Input() postID!: string;
  isLiked: boolean = false;
  countLike: number = 0;
  likeRequest: LikeRequest = {}


  constructor(
    private likeService: LikeService,
  ) { }


  ngOnInit(): void {
    this.getIsLiked()
    this.countLikes()

  }

  countLikes() {
    this.likeService.countLikes(this.likeRequest.postID!).subscribe({
      next: (apiResponse: ApiResponse<number>) => {
        const countLikesResponse = apiResponse.result

        if (countLikesResponse) {
          this.countLike = countLikesResponse;
        }
      },
      error: (apiResponse: ApiResponse<boolean>) => {
        console.error('error', 'Error', apiResponse.message ?? '');
      }
    })

  }

  getIsLiked() {
    this.likeRequest.postID = this.postID
    this.likeService.isLiked(this.likeRequest).subscribe({
      next: (apiResponse: ApiResponse<boolean>) => {
        const isLikedResponse = apiResponse.result

        if (isLikedResponse) {
          this.isLiked = isLikedResponse;
        }
      },
      error: (apiResponse: ApiResponse<boolean>) => {
        console.error('error', 'Error', apiResponse.message ?? '');
        console.log(apiResponse);
      }
    })
  }

  toggleLike() {
    this.likeRequest.postID = this.postID
    this.likeService.toggle(this.likeRequest).subscribe({
      next: (apiResponse: ApiResponse<LikeResponse>) => {
        const likeResponse = apiResponse.result

        if (likeResponse?.id) {
          this.isLiked = true;
          this.countLike++
        }
        else {
          this.isLiked = false;
          this.countLike--
        }
      }
    })
  }

}
