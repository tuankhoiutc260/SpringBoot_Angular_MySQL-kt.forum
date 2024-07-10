import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PostRequest } from '../../../core/interface/request/post-request';
import { HttpErrorResponse } from '@angular/common/http';
import {  SafeResourceUrl } from '@angular/platform-browser';



@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
  providers: [MessageService, ConfirmationService]
})
export class FeedComponent implements OnInit {
  isVisible: boolean = false;

  isEdit: boolean = false;

  isImageValid: boolean = true

  isContentValid: boolean = true;

  isTitleValid: boolean = true;

  isTagsValid: boolean = true;

  isActiveImage: boolean = false;

  fileName: string = '';

  imagePreview: SafeResourceUrl | null = null; // Đảm bảo bạn đã import SafeResourceUrl từ @angular/platform-browser

  postContent?: {
    html: string | undefined,
    text: string | undefined,
  }

  postsResponse: PostResponse[] = [];

  postRequest: PostRequest = {}

  postResponse: PostResponse = {}

  constructor(
    private postService: PostService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
  }

  ngOnInit() {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.findAll().subscribe({
      next: (apiResponse: ApiResponse<PostResponse[]>) => {
        const postResponseList = apiResponse.result;
        if (postResponseList) {
          this.postsResponse = postResponseList;
        } else {
          console.error('No result found in response: ', apiResponse.message);
        }
      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        console.error(httpErrorResponse.error.message)
      }
    });
  }

  getHtmlContent(): string {
    const htmlContent = typeof this.postContent === 'object'
      && this.postContent !== null
      && 'html' in this.postContent
      ? this.postContent.html
      : this.postContent as unknown as string;
    return htmlContent!
  }

  onCreatePost() {
    this.validateFields()
    if (this.isImageValid && this.isImageValid && this.isTitleValid && this.isContentValid && this.isTagsValid) {
      this.postRequest.content = this.getHtmlContent();
      this.postService.create(this.postRequest).subscribe({
        next: () => {
          this.showMessage('info', 'Confirmed', 'Post created')
          this.isVisible = false;
          this.onLoadPage()
        },
        error: (apiResponse: ApiResponse<PostResponse>) => {
          console.error('Error creating post:', apiResponse);
        }
      });
    }
  }

  onUpdatePost(): void {
    this.postRequest.content = this.getHtmlContent();
    this.postService.update(this.postResponse.id!, this.postRequest).subscribe({
      next: () => {
        this.showMessage('info', 'Confirmed', 'Post updated')
        this.isVisible = false;
        this.onLoadPage()
      },
      error: (apiResponse: ApiResponse<PostResponse>) => {
        console.error(apiResponse);
      }
    });
  }

  onSubmit(){
    if(this.postResponse.id){
      this.onUpdatePost()
    }
    else{
      this.onCreatePost()
    }
  }

  onDeletePost(event: Event) {
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
            this.onLoadPage()
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

  validateFields() {
    this.isTitleValid = this.postRequest.title !== undefined && this.postRequest.title.trim() !== '';
    this.isTagsValid = this.postRequest.tags != undefined && this.postRequest.tags.toString() !== ''
    this.isContentValid = typeof (this.postContent) == 'object'
      ? !(this.postContent?.html === undefined && this.postContent?.text === undefined)
      : this.postContent !== null

  }

  onSelectFile(event: any): void {
    const selectedFiles = event.target.files;

    if (selectedFiles.length > 0) {
      const file: File = event.target.files[0];
      const mimeType = file.type;

      if (mimeType.match(/image\/*/) == null) {
        this.isImageValid = false;
        this.isActiveImage = false;
        this.imagePreview = ''
        this.fileName = ''
        return;
      }
      else {
        this.isImageValid = false;
        this.isImageValid = true;
      }
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = reader.result;
        this.isActiveImage = true;
        this.fileName = file.name
      }
      this.postRequest.image = file;
      reader.readAsDataURL(file);
    }
  }

  openDialogNew() {
    this.isEdit = false;
    this.isVisible = true
    this.isActiveImage = false
    this.imagePreview = null
    this.postRequest = {}
    this.postContent = { html: this.postRequest.content, text: undefined };
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

  openDialogEdit(postResponse: PostResponse) {
    this.isEdit = true;
    this.isVisible = true;
    this.postResponse = { ...postResponse };

    if (postResponse.image) {
      this.imagePreview = ('data:image/png;base64,'
        + postResponse.image);
      this.isActiveImage = true

      const base64 = postResponse.image!;
      const imageName = postResponse.title!;
      this.fileName = imageName;
      const imageBlob = this.dataURItoBlob(base64);
      const imageFile = new File([imageBlob], imageName, { type: 'image/png' });
      this.postRequest.image = imageFile;
    }
    else {
      this.imagePreview = null
      this.isActiveImage = false
      this.fileName = '';
      this.postRequest.image = null;
    }

    this.postRequest.title = postResponse.title;
    this.postRequest.tags = postResponse.tags;
    this.postContent = { html: postResponse.content, text: undefined };

    const htmlContent = typeof this.postContent === 'object'
      && this.postContent !== null
      && 'html' in this.postContent
      ? this.postContent.html
      : this.postContent as unknown as string;

    this.postRequest.content = htmlContent
  }

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
    this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  }
}
