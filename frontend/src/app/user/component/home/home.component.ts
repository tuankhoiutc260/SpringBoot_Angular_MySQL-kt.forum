import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, catchError, throwError, takeUntil, Subject, tap } from 'rxjs';
import { CategoryResponse } from '../../../api/model/response/category-response';
import { PagedResponse } from '../../../api/model/response/paged-response';
import { SubCategoryResponse } from '../../../api/model/response/sub-category-response';
import { CategoryApiService } from '../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../api/service/rest-api/sub-category-api.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  categoryResponsePage$!: Observable<PagedResponse<CategoryResponse[]>>;
  readonly CATEGORIES_PAGE_SIZE = 20;

  subCategoriesMap = new Map<string, Observable<PagedResponse<SubCategoryResponse[]>>>();
  readonly SUB_CATEGORY_PAGE_SIZE = 12;

  private destroy$ = new Subject<void>();

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService
  ) { }

  ngOnInit() {
    this.initializeCategories(0, this.CATEGORIES_PAGE_SIZE);
  }

  initializeCategories(page: number, size: number) {
    this.categoryResponsePage$ = this.categoryApiService.getAll(page, size)
      .pipe(
        tap(categoryResponsePage => {
          categoryResponsePage.content.forEach(category => {
            this.initializeSubCategories(category.id, 0, this.SUB_CATEGORY_PAGE_SIZE);
          });
        }),
        catchError(error => {
          console.error('Error loading categories', error);
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      );
  }

  initializeSubCategories(categoryId: string, page: number, size: number) {
    this.subCategoriesMap.set(
      categoryId,
      this.subCategoryApiService.getByCategoryId(categoryId, page, size).pipe(
        catchError(error => {
          console.error('Error fetching Sub Categories', error);
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      )
    );
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
