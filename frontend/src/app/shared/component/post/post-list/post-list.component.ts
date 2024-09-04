import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { ActivatedRoute, Router } from '@angular/router';
import { of, Subject, BehaviorSubject } from 'rxjs';
import { catchError, map, takeUntil, switchMap, distinctUntilChanged, shareReplay, finalize } from 'rxjs/operators';
import { PagedResponse } from '../../../../api/model/response/paged-response';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PostListComponent implements OnInit, OnDestroy {
  subCategoryId!: string;
  subCategoryTitleSlug!: string;
  categoryTitleSlug!: string;

  isDialogVisible = false;
  readonly POSTS_PAGE_SIZE = 10;
  postsCurrentPage = 0;
  postResponsePage$ = new BehaviorSubject<PagedResponse<PostResponse[]> | null>(null);

  private destroy$ = new Subject<void>();
  isLoading$ = new BehaviorSubject<boolean>(false);
  error$ = new BehaviorSubject<string | null>(null);

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private postApiService: PostApiService,
  ) { }

  ngOnInit() {
    this.setupRouteSubscription();
  }

  private setupRouteSubscription() {
    this.activatedRoute.params.pipe(
      takeUntil(this.destroy$),
      distinctUntilChanged((prev, curr) =>
        prev['categoryTitleSlug'] === curr['categoryTitleSlug'] &&
        prev['subcategoryTitleSlug'] === curr['subcategoryTitleSlug'] &&
        prev['subcategoryId'] === curr['subcategoryId']
      ),
      switchMap(params => {
        this.subCategoryId = params['subcategoryId'];
        this.subCategoryTitleSlug = params['subcategoryTitleSlug'];
        this.categoryTitleSlug = params['categoryTitleSlug'];

        return this.activatedRoute.queryParams.pipe(
          map(queryParams => {
            this.postsCurrentPage = queryParams['page'] ? parseInt(queryParams['page']) : 0;
            return queryParams;
          })
        );
      }),
      shareReplay(1)
    ).subscribe(() => {
      this.getPostsBySubCategoryId(this.subCategoryId);
    });
  }

  getPostsBySubCategoryId(subCategoryId: string) {
    this.isLoading$.next(true);
    this.error$.next(null);

    this.postApiService.getBySubCategoryId(subCategoryId, this.postsCurrentPage, this.POSTS_PAGE_SIZE)
      .pipe(
        map(postResponsePage => postResponsePage),
        catchError(error => {
          console.error("Error fetching Posts", error);
          this.error$.next("Failed to load posts. Please try again later.");
          return of({ content: [], page: { size: 0, number: 0, totalElements: 0, totalPages: 0 } });
        }),
        finalize(() => this.isLoading$.next(false)),
        takeUntil(this.destroy$)
      )
      .subscribe(postsPage => {
        this.postResponsePage$.next(postsPage);
      });
  }

  onPageChange(event: any) {
    this.postsCurrentPage = event.page;
    this.updateUrlAndFetchPosts();
  }

  updateUrlAndFetchPosts() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: this.postsCurrentPage },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.getPostsBySubCategoryId(this.subCategoryId);
    });
  }

  onCreatePost() {
    this.isDialogVisible = true;
  }

  trackByPostId(index: number, postResponse: PostResponse): string {
    return postResponse.id;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
