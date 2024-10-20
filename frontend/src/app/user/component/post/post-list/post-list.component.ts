import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, Input, OnChanges, SimpleChanges, Output, EventEmitter, ElementRef, ViewChild } from '@angular/core';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, combineLatest, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';
import { PagedResponse } from '../../../../api/model/response/paged-response';
import { AuthService } from '../../../../core/service/auth.service';
import { AuthApiService } from '../../../../api/service/rest-api/auth-api.service';
import { MessageService } from 'primeng/api';
import { SearchType } from '../../../../api/model/enum/search-type';

interface SearchState {
  key: string;
  type: SearchType;
  isActive: boolean;
}

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.scss',
  providers: [MessageService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PostListComponent implements OnInit, OnDestroy, OnChanges {
  @ViewChild('searchPost') searchPostInput!: ElementRef<HTMLInputElement>;

  @Input() subCategoryIdInput!: string;
  @Output() pageChangeClicked = new EventEmitter<void>();

  subCategoryTitleSlug = '';
  categoryTitleSlug = '';
  isAuthenticated$: Observable<boolean>;
  isDialogCreatePostVisible = false;

  readonly POSTS_PAGE_SIZE = 10;
  private postsCurrentPage$ = new BehaviorSubject<number>(0);
  postResponsePage$: Observable<PagedResponse<PostResponse[]>>;

  selectedSearchOptions: string[] = [];

  private searchState$ = new BehaviorSubject<SearchState>({
    key: '',
    type: SearchType.BOTH,
    isActive: false
  });

  private destroy$ = new Subject<void>();
  private refresh$ = new BehaviorSubject<void>(undefined);

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private postApiService: PostApiService,
    public authApiService: AuthApiService,
    private authService: AuthService,
    private messageService: MessageService,
  ) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.postResponsePage$ = this.initPostResponsePage();
  }

  private initPostResponsePage(): Observable<PagedResponse<PostResponse[]>> {
    return combineLatest([
      this.postsCurrentPage$,
      this.searchState$,
      this.refresh$
    ]).pipe(
      debounceTime(300),
      switchMap(([page, searchState]) =>
        searchState.isActive
          ? this.searchPostsWithSubCategoryId(searchState.key, this.subCategoryIdInput, page, this.POSTS_PAGE_SIZE, searchState.type)
          : this.fetchPosts(page)
      ),
      shareReplay(1)
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['subCategoryIdInput']) {
      this.refresh$.next();
    }
  }

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
      tap(params => {
        this.categoryTitleSlug = params['categoryTitleSlug'];
        this.subCategoryTitleSlug = params['subcategoryTitleSlug'];
        this.subCategoryIdInput = params['subcategoryId'];
      }),
      switchMap(() => this.activatedRoute.queryParams),
      tap(queryParams => {
        const page = queryParams['page'] ? parseInt(queryParams['page'], 10) : 0;
        this.postsCurrentPage$.next(page);
      }),
      tap(() => {
        this.searchState$.next({
          key: '',
          type: SearchType.BOTH,
          isActive: false
        });
        this.selectedSearchOptions = [];
  
        if (this.searchPostInput) {
          this.searchPostInput.nativeElement.value = '';
        }
  
        this.refresh$.next();
      })
    ).subscribe(() => {
      this.searchState$.next({ ...this.searchState$.value, isActive: false });
      this.refresh$.next();
    });
  }

  private fetchPosts(page: number): Observable<PagedResponse<PostResponse[]>> {
    return this.postApiService.getBySubCategoryId(this.subCategoryIdInput, page, this.POSTS_PAGE_SIZE).pipe(
      catchError(error => {
        this.handleError('Error fetching Posts', error);
        return of({ content: [], page: { size: 0, number: 0, totalElements: 0, totalPages: 0 } });
      })
    );
  }

  onSearchPost(key: string) {
    let searchType: SearchType;

    if (this.selectedSearchOptions.includes('search-by-post-title') && this.selectedSearchOptions.includes('search-by-post-content')) {
      searchType = SearchType.BOTH;
    } else if (this.selectedSearchOptions.includes('search-by-post-title')) {
      searchType = SearchType.TITLE;
    } else if (this.selectedSearchOptions.includes('search-by-post-content')) {
      searchType = SearchType.CONTENT;
    } else {
      searchType = SearchType.BOTH;
    }

    this.searchState$.next({
      key,
      type: searchType,
      isActive: true
    });
    this.postsCurrentPage$.next(0);
  }

  searchPostsWithSubCategoryId(key: string, subCategoryId: string, page: number, size: number, searchType: SearchType): Observable<PagedResponse<PostResponse[]>> {
    return this.postApiService.searchBySubCategoryId(key, subCategoryId, page, size, searchType).pipe(
      catchError(error => {
        this.handleError('Error during Search:', error);
        return of({ content: [], page: { size: 0, number: 0, totalElements: 0, totalPages: 0 } });
      })
    );
  }

  activateSignUp(value: boolean) {
    this.authService.setSignUpActive(value);
  }

  onPageChange(event: any) {
    this.postsCurrentPage$.next(event.page);
    this.pageChangeClicked.emit();
    this.updateUrlAndFetchPosts(event.page);
  }

  private updateUrlAndFetchPosts(page: number) {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page },
      queryParamsHandling: 'merge'
    });
  }

  onCreatePost() {
    this.isDialogCreatePostVisible = true;
  }

  trackByPostId(_index: number, postResponse: PostResponse): string {
    return postResponse.id;
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
