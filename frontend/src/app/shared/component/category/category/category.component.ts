import { Component, OnInit, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, of, Subject, throwError } from 'rxjs';
import { catchError, map, switchMap, takeUntil } from 'rxjs/operators';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { PagedResponse } from '../../../../api/model/response/paged-response';

interface CategoryState {
  subCategories: SubCategoryResponse[];
  currentPage: number;
  isLoading: boolean;
  noMoreData: boolean;
}

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit, OnDestroy {
  categoryResponsePage$!: Observable<PagedResponse<CategoryResponse[]>>;
  categoryStates = new Map<string, BehaviorSubject<CategoryState>>();

  readonly CATEGORIES_PAGE_SIZE = 20;
  readonly SUB_CATEGORY_PAGE_SIZE = 4;

  private destroy$ = new Subject<void>();

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService,
  ) { }

  ngOnInit() {
    this.initializeCategories();
  }

  initializeCategories(page: number = 0) {
    this.categoryResponsePage$ = this.categoryApiService.getAll(page, this.CATEGORIES_PAGE_SIZE).pipe(
      switchMap(categoryResponsePage => {
        categoryResponsePage.content.forEach(category => {
          this.initializeCategoryState(category.id);
          this.loadSubCategories(category.id);
        });
        return of(categoryResponsePage);
      }),
      catchError(error => {
        console.error('Error loading categories', error);
        return throwError(() => new Error(error.message || 'Server error'));
      }),
      takeUntil(this.destroy$)
    );
  }

  private initializeCategoryState(categoryId: string): void {
    if (!this.categoryStates.has(categoryId)) {
      const initialState: CategoryState = {
        subCategories: [],
        currentPage: 0,
        isLoading: false,
        noMoreData: false
      };
      this.categoryStates.set(categoryId, new BehaviorSubject<CategoryState>(initialState));
    }
  }

  loadSubCategories(categoryId: string) {
    const currentSubCategoryState = this.categoryStates.get(categoryId)?.value;
    if (!currentSubCategoryState || currentSubCategoryState.isLoading || currentSubCategoryState.noMoreData) return;

    this.updateCategoryState(categoryId, { isLoading: true });

    this.subCategoryApiService.getByCategoryId(categoryId, currentSubCategoryState.currentPage, this.SUB_CATEGORY_PAGE_SIZE)
      .pipe(
        map(subCategoryResponsePage => subCategoryResponsePage),
        catchError(error => {
          console.error('Error fetching Sub Categories', error);
          this.updateCategoryState(categoryId, { isLoading: false });
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(newSubCategoryPage => {
        const currentSubCategoryState = this.categoryStates.get(categoryId)?.value;
        if (currentSubCategoryState) {
          const updatedSubCategoryState = [...currentSubCategoryState.subCategories, ...newSubCategoryPage.content];
          const noMoreSubCategoriesData = newSubCategoryPage.content.length < this.SUB_CATEGORY_PAGE_SIZE;

          this.updateCategoryState(categoryId, {
            subCategories: updatedSubCategoryState,
            currentPage: currentSubCategoryState.currentPage + 1,
            isLoading: false,
            noMoreData: noMoreSubCategoriesData
          });
        }
      });
  }

  private updateCategoryState(categoryId: string, newSubCategoryState: Partial<CategoryState>) {
    const currentSubCategoryState = this.categoryStates.get(categoryId);
    if (currentSubCategoryState) {
      currentSubCategoryState.next({
        ...currentSubCategoryState.value,
        ...newSubCategoryState
      });
    }
  }

  getCategoryState(categoryId: string): Observable<CategoryState> {
    return this.categoryStates.get(categoryId)?.asObservable() ??
      of({ subCategories: [], currentPage: 0, isLoading: false, noMoreData: false });
  }

  showMoreSubCategories(categoryId: string) {
    this.loadSubCategories(categoryId);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
