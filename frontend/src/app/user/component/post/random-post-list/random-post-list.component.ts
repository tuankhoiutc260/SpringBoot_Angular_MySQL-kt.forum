import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, of, Subject, takeUntil } from 'rxjs';
import { PostResponse } from '../../../../api/model/response/post-response';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-random-post-list',
  templateUrl: './random-post-list.component.html',
  styleUrl: './random-post-list.component.scss',
  providers: [MessageService]
})
export class RandomPostListComponent implements OnInit, OnDestroy {
  postResponseList$ = new BehaviorSubject<PostResponse[]>([]);
  responsiveOptions: any[] | undefined;

  private destroy$ = new Subject<void>();
  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.initializeRandomPostList();

    this.responsiveOptions = [
      {
        breakpoint: '1199px',
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

  initializeRandomPostList() {
    this.postApiService.get6RandomPosts(null).pipe(
      catchError(error => {
        console.error('Error loading posts', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error loading Posts'
        });
        return of([]);
      }),
      takeUntil(this.destroy$)
    ).subscribe(posts => {
      this.postResponseList$.next(posts);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
