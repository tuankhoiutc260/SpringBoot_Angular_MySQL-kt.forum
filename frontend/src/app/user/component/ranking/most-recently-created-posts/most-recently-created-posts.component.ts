import { Component, OnDestroy, OnInit } from '@angular/core';
import { catchError, Observable, of, Subject } from 'rxjs';
import { MessageService } from 'primeng/api';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';

@Component({
  selector: 'app-most-recently-created-posts',
  templateUrl: './most-recently-created-posts.component.html',
  styleUrl: './most-recently-created-posts.component.scss'
})
export class MostRecentlyCreatedPostsComponent implements OnInit {
  postResponseList$!: Observable<PostResponse[]>;

  constructor( 
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
      this.get5MostRecentlyCreatedPosts();
  }

  get5MostRecentlyCreatedPosts() {
    this.postResponseList$ = this.postApiService.get5MostRecentlyCreatedPosts()
      .pipe(
        catchError(error => {
          this.handleError('Failed to load Most recnetly Posts', error);
          return of([]);
        })
      );
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }
}
