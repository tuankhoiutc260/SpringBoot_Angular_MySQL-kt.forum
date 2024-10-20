import { Component } from '@angular/core';
import { MessageService } from 'primeng/api';
import { switchMap, tap, forkJoin, map, of, catchError, shareReplay, takeUntil, Observable, BehaviorSubject, Subject } from 'rxjs';
import { PagedResponse } from '../../../../api/model/response/paged-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { CategoryResponse } from '../../../../api/model/response/category-response';

interface CategoryWithSubCategories extends CategoryResponse {
  subCategoryResponsePaged$: Observable<PagedResponse<SubCategoryResponse[]>>;
  firstSubCategoryId: string;
  firstSubCategoryTitle: string;
}
@Component({
  selector: 'app-category-list-home',
  templateUrl: './category-list-home.component.html',
  styleUrl: './category-list-home.component.scss',
  providers: [MessageService],
})

export class CategoryListHomeComponent {
  private readonly CATEGORIES_PAGE_SIZE = 6;
  private readonly SUB_CATEGORY_PAGE_SIZE = 2;

  private categoriesPage$ = new BehaviorSubject<number>(0);
  categoriesWithSubCategories$!: Observable<CategoryWithSubCategories[]>;

  firstCategoryId$ = new BehaviorSubject<string>('');
  firstCategoryTitle$ = new BehaviorSubject<string>('');
  firstSubCategoryId$ = new BehaviorSubject<string>('');
  firstSubCategoryTitle$ = new BehaviorSubject<string>('');

  private destroy$ = new Subject<void>();

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit() {
    this.initializeCategories();
  }

  private initializeCategories() {
    this.categoriesWithSubCategories$ = this.categoriesPage$.pipe(
      switchMap(page => this.categoryApiService.getAll(page, this.CATEGORIES_PAGE_SIZE)),
      tap(categoryResponsePaged => {
        if (categoryResponsePaged.content.length > 0) {
          this.firstCategoryId$.next(categoryResponsePaged.content[0].id);
          this.firstCategoryTitle$.next(categoryResponsePaged.content[0].title);
        }
      }),
      switchMap(categoryResponsePaged =>
        forkJoin(categoryResponsePaged.content.map((category, index) =>
          this.initializeSubCategories(category.id).pipe(
            map(subCategoryResponsePaged => {
              const firstSubCategory = subCategoryResponsePaged.content[0] || { id: '', title: '' };
              if (index === 0) {
                this.firstSubCategoryId$.next(firstSubCategory.id);
                this.firstSubCategoryTitle$.next(firstSubCategory.title);
              }
              return {
                ...category,
                subCategoryResponsePaged$: of(subCategoryResponsePaged),
                firstSubCategoryId: firstSubCategory.id,
                firstSubCategoryTitle: firstSubCategory.title
              };
            })
          )
        ))
      ),
      catchError(error => {
        console.error('Error loading categories', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error loading Category' });
        return of([]);
      }),
      shareReplay(1),
      takeUntil(this.destroy$)
    );
  }

  private initializeSubCategories(categoryId: string): Observable<PagedResponse<SubCategoryResponse[]>> {
    return this.subCategoryApiService.getByCategoryId(categoryId, 0, this.SUB_CATEGORY_PAGE_SIZE).pipe(
      catchError(error => {
        console.error('Error loading subcategories', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error loading Topic' });
        return of({
          content: [],
          page: { totalElements: 0, totalPages: 0, size: 0, number: 0 }
        } as PagedResponse<SubCategoryResponse[]>);
      }),
      shareReplay(1)
    );
  }

  loadMoreCategories() {
    this.categoriesPage$.next(this.categoriesPage$.value + 1);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
