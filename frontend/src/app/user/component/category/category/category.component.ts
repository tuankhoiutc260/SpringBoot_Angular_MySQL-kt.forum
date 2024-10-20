import { Component, OnInit, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, takeUntil, tap, switchMap, distinctUntilChanged } from 'rxjs/operators';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { PagedResponse } from '../../../../api/model/response/paged-response';
import { ActivatedRoute, Router } from '@angular/router';
import { ToSlugPipe } from '../../../../core/pipe/to-slug.pipe';
import { AuthService } from '../../../../core/service/auth.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss'],
  providers: [ToSlugPipe, MessageService]
})
export class CategoryComponent implements OnInit, OnDestroy {
  private activeCategoryId$ = new BehaviorSubject<string>('');
  public activeSubCategoryId$ = new BehaviorSubject<string>('');

  activeCategorySlug = '';
  activeSubCategorySlug = '';

  categoryResponsePage$!: Observable<PagedResponse<CategoryResponse[]>>;
  subCategoryResponsePage$!: Observable<PagedResponse<SubCategoryResponse[]>>;

  categoryIndex = 0;

  readonly CATEGORIES_PAGE_SIZE = 20;
  readonly SUB_CATEGORY_PAGE_SIZE = 30;
  subCategoryCurrentPage = 0;

  isAuthenticated$: Observable<boolean>;

  private destroy$ = new Subject<void>();

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toSlugPipe: ToSlugPipe,
    private authService: AuthService,
    private messageService: MessageService

  ) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
  }

  ngOnInit() {
    this.setupRouteSubscription();
    this.initializeCategories();
    this.initializeSubCategories();
  }

  private setupRouteSubscription() {
    this.activatedRoute.params.pipe(
      takeUntil(this.destroy$),
      tap(params => {
        this.activeCategoryId$.next(params['categoryId']);
        this.activeSubCategoryId$.next(params['subcategoryId']);
        this.activeCategorySlug = params['categorySlug'];
        this.activeSubCategorySlug = params['subcategorySlug'];
      })
    ).subscribe();
  }

  private initializeCategories() {
    this.categoryResponsePage$ = this.activeCategoryId$.pipe(
      switchMap(() => this.categoryApiService.getAll(0, this.CATEGORIES_PAGE_SIZE)),
      tap(categoryResponsePage => {
        this.categoryIndex = categoryResponsePage.content.findIndex(category => category.id === this.activeCategoryId$.value);
      }),
      catchError(error => {
        this.handleError('Error loading Categories', error);
        return of({
          content: [],
          page: { totalElements: 0, totalPages: 0, size: 0, number: 0 }
        } as PagedResponse<CategoryResponse[]>);
      })
    );
  }

  private initializeSubCategories() {
    this.subCategoryResponsePage$ = this.activeCategoryId$.pipe(
      distinctUntilChanged(),
      switchMap(categoryId => this.subCategoryApiService.getByCategoryId(categoryId, this.subCategoryCurrentPage, this.SUB_CATEGORY_PAGE_SIZE)),
      tap(subCategoryResponsePage => {
        if (subCategoryResponsePage.content.length > 0 && !this.activeSubCategoryId$.value) {
          const firstSubCategory = subCategoryResponsePage.content[0];
          this.setActiveSubCategory(firstSubCategory.id, firstSubCategory.title);
        }
      }),
      catchError(error => {
        this.handleError('Error loading Subcategories', error);
        return of({
          content: [],
          page: { totalElements: 0, totalPages: 0, size: 0, number: 0 }
        } as PagedResponse<SubCategoryResponse[]>);
      })
    );
  }

  setActiveSubCategory(subCategoryId: string, subCategoryTitle: string) {
    this.activeSubCategoryId$.next(subCategoryId);
    this.activeSubCategorySlug = this.toSlugPipe.transform(subCategoryTitle);
    this.updateUrl();
  }

  setActiveCategory(categoryId: string, categoryTitle: string) {
    this.activeCategoryId$.next(categoryId);
    this.activeCategorySlug = this.toSlugPipe.transform(categoryTitle);
    this.activeSubCategoryId$.next('');
    this.activeSubCategorySlug = '';
    this.subCategoryCurrentPage = 0;
  }

  private updateUrl() {
    if (this.activeSubCategoryId$.value) {
      this.router.navigate([
        this.activeCategorySlug,
        this.activeCategoryId$.value,
        this.activeSubCategorySlug,
        this.activeSubCategoryId$.value,
        'posts'
      ]);
    } else {
      this.router.navigate([
        this.activeCategorySlug,
        this.activeCategoryId$.value
      ]);
    }
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
