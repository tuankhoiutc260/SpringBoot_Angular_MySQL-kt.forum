import { Component, OnDestroy, OnInit } from '@angular/core';
import { catchError, Observable, of, Subject } from 'rxjs';
import { MessageService } from 'primeng/api';
import { PostResponse } from '../../../../api/model/response/post-response';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';

@Component({
  selector: 'app-top-trending-posts',
  templateUrl: './top-trending-posts.component.html',
  styleUrl: './top-trending-posts.component.scss',
  providers: [MessageService],
})
export class TopTrendingPostsComponent implements OnInit {
  postResponseList$!: Observable<PostResponse[]>;

  responsiveOptions: any[] | undefined;

  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.getTopTrendingPosts();

    this.responsiveOptions = [
      {
        breakpoint: '1345px',
        numVisible: 2,
        numScroll: 1
      },
      {
        breakpoint: '700px',
        numVisible: 1,
        numScroll: 1
      }
    ];
  }

  getTopTrendingPosts() {
    this.postResponseList$ = this.postApiService.getTop3OrderByLikesDesc()
      .pipe(
        catchError(error => {
          this.handleError('Failed to load Top treding Posts', error);
          return of([]);
        })
      );
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }
}
