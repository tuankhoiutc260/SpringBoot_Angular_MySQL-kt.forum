import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { PostApiService } from '../../../../api/service/post-api.service';
import { MessageService } from 'primeng/api';
import { PostRequest } from '../../../../api/model/request/post-request';
import { ApiResponse } from '../../../../api/model/response/apiResponse';
import { PostResponse } from '../../../../api/model/response/post-response';

@Component({
  selector: 'app-create-update-post',
  templateUrl: './create-update-post.component.html',
  styleUrl: './create-update-post.component.scss',
  providers: [MessageService]
})
export class CreateUpdatePostComponent implements OnInit, OnDestroy {
  @Input() isDialogVisible!: boolean
  @Input() postResponse!: PostResponse
  @Output() isDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  onDialogHide() {
    this.isDialogVisible = false;
    this.isDialogVisibleChange.emit(false);
  }

  isEdit: boolean = false;
  postRequest: PostRequest = {}
  imagePreview: SafeResourceUrl | null = null;
  isActiveImage: boolean = false;
  fileName: string = '';
  postContent?: {
    html: string | undefined,
    text: string | undefined,
  }
  private subscription: Subscription = new Subscription();

  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.initializeForm();
  }

  ngOnChanges(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    if (this.postResponse.image) {
      this.imagePreview = 'data:image/png;base64,' + this.postResponse.image;
      this.isActiveImage = true;

      const base64 = this.postResponse.image!;
      const imageName = this.postResponse.title!;
      this.fileName = imageName;
      const imageBlob = this.dataURItoBlob(base64);
      const imageFile = new File([imageBlob], imageName, { type: 'image/png' });
      this.postRequest.image = imageFile;
    } else {
      this.imagePreview = null;
      this.isActiveImage = false;
      this.fileName = '';
      this.postRequest.image = null;
    }

    this.postRequest.title = this.postResponse.title;
    this.postRequest.tags = this.postResponse.tags;
    this.postContent = { html: this.postResponse.content, text: undefined };

    const htmlContent = this.postContent?.html ?? this.postContent as unknown as string;
    this.postRequest.content = htmlContent;
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

  isImageValid: boolean = true
  isTitleValid: boolean = true;
  isContentValid: boolean = true;
  isTagsValid: boolean = true;

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

  validateFields() {
    this.isTitleValid = this.postRequest.title !== undefined && this.postRequest.title.trim() !== '';
    this.isTagsValid = this.postRequest.tags != undefined && this.postRequest.tags.toString() !== ''
    this.isContentValid = typeof (this.postContent) == 'object'
      ? !(this.postContent?.html === undefined && this.postContent?.text === undefined)
      : this.postContent !== null
  }

  getHtmlContent(): string {
    const htmlContent = typeof this.postContent === 'object'
      && this.postContent !== null
      && 'html' in this.postContent
      ? this.postContent.html
      : this.postContent as unknown as string;
    return htmlContent!
  }

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
    this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  }

  onSubmit() {
    if (this.postResponse.id) {
      this.onUpdatePost()
    }
    else {
      this.onCreatePost()
    }
  }

  onCreatePost() {
    this.validateFields()
    if (this.isImageValid && this.isImageValid && this.isTitleValid && this.isContentValid && this.isTagsValid) {
      this.postRequest.content = this.getHtmlContent();
      this.postApiService.create(this.postRequest).subscribe({
        next: () => {
          this.showMessage('info', 'Confirmed', 'Post created')
          this.isDialogVisible = false;
          this.onLoadPage()
        },
        error: (apiResponse: ApiResponse<PostResponse>) => {
          console.error('Error creating post:', apiResponse);
        }
      });
    }
  }

  onUpdatePost() {
    this.validateFields()
    if (this.isImageValid && this.isImageValid && this.isTitleValid && this.isContentValid && this.isTagsValid) {
      this.postRequest.content = this.getHtmlContent();
      const sub = this.postApiService.update(this.postResponse.id!, this.postRequest).subscribe({
        next: (apiResponse: ApiResponse<PostResponse>) => {
          const postUpdated = apiResponse.result;
          if (postUpdated) {
            this.showMessage('info', 'Confirmed', 'Post updated')
            this.isDialogVisible = false;
            this.onLoadPage()
          } else {
            console.error('No result found in response:', apiResponse.message);
          }
        },
        error: (error) => {
          console.error('Error Updating Post', error);
        }
      });
      this.subscription.add(sub);
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
