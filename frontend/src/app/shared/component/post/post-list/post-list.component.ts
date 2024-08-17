import { Component, OnInit, OnDestroy } from '@angular/core';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { catchError, map, takeUntil, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.scss',
})
export class PostListComponent implements OnInit, OnDestroy {
  totalPosts: number = 0;
  postsPageSize: number = 10;
  postsCurrentPage: number = 1;

  subCategoryId!: string;
  subCategoryTitleSlug!: string;
  categoryTitleSlug!: string;

  postResponseList$!: Observable<PostResponse[]>

  isDialogVisible: boolean = false;


  private destroy$ = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private postApiService: PostApiService,
  ) { }

  ngOnInit() {
    this.activatedRoute.params.pipe(
      takeUntil(this.destroy$),
      switchMap(params => {
        this.subCategoryId = params['subcategoryId'];
        this.subCategoryTitleSlug = params['subcategorySlug'];
        this.categoryTitleSlug = params['categorySlug'];
        
        return this.activatedRoute.queryParams;
      })
    ).subscribe(queryParams => {
      this.postsCurrentPage = queryParams['page'] ? parseInt(queryParams['page']) : 1;
      this.getPostsBySubCategoryId();
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getPostsBySubCategoryId() {
    const apiPage = this.postsCurrentPage - 1;
    this.postResponseList$ = this.postApiService.findBySubCategoryId(this.subCategoryId, apiPage, this.postsPageSize)
      .pipe(
        map((apiResponse: ApiResponse<PostResponse[]>) => {
          return apiResponse.result || [];
        }),
        catchError(error => {
          console.error("Error fetching Posts", error);
          return of([]);
        })
      );
  }

  onPageChange(event: any) {
    this.postsCurrentPage = event.page + 1; 
    this.updateUrlAndFetchPosts();
  }

  onCreatePost(){
    this.isDialogVisible = true;
  }

  private updateUrlAndFetchPosts() {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: { page: this.postsCurrentPage },
      queryParamsHandling: 'merge'
    }).then(() => {
      this.getPostsBySubCategoryId();
    });
  }
}