import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PostResponse } from '../../../core/interface/response/post-response';
import { SafeResourceUrl } from '@angular/platform-browser';
import { UserResponse } from '../../../core/interface/response/user-response';
import { UserService } from '../../../core/service/user.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent {
  @Input() postResponse!: PostResponse;
  @Input() canEdit!: boolean;
  @Output() isEditing = new EventEmitter();

  imagePreview: SafeResourceUrl | null = null;
  fileName: string = '';
  minRead: number = 0

  userResponse: UserResponse = {}

  constructor(
    private userService: UserService
  ){}

  ngOnInit(): void {
      if (this.postResponse.image) {
        this.imagePreview = ('data:image/png;base64,'
          + this.postResponse.image);
  
        const base64 = this.postResponse.image!;
        const imageName = this.postResponse.title!;
        this.fileName = imageName;
        const imageBlob = this.dataURItoBlob(base64);
        const imageFile = new File([imageBlob], imageName, { type: 'image/png' });
      }

      this.minRead = this.calculateMinRead(this.postResponse.content!)
      this.onGetInfoUser();
  }

  dataURItoBlob(dataURI: string) {
    const byteString = window.atob(dataURI);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([int8Array], { type: 'image/png' });
    return blob;
  }

  calculateMinRead(text: string): number {
    const wordsPerMinute = 200;
    const cleanText = text.replace(/[^\w\s]/gi, ''); 
    const textLength = cleanText.split(/\s+/).length; 
    return Math.ceil(textLength / wordsPerMinute);
  }

  get(){
    console.log(this.postResponse.createdBy)

  }
 userImage: string = ''
  onGetInfoUser(){
    this.userService.findByUserName(this.postResponse.createdBy!).subscribe({
      next: (apiResponse: ApiResponse<UserResponse>)=>{
        this.userResponse = apiResponse.result!

      },
      error: (httpErrorResponse: HttpErrorResponse)=>{
        console.error(httpErrorResponse)
      }
    })
  }
}
