import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, of, tap } from 'rxjs';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { ApiResponse } from '../../../../api/model/response/api-response';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';

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

export class CategoryComponent implements OnInit {
  categorieResponseList$!: Observable<CategoryResponse[]>;
  categoryStates: { [categoryId: string]: BehaviorSubject<CategoryState> } = {};
  subCategoryItemsPerPage = 4;

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService
  ) { }

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.categorieResponseList$ = this.categoryApiService.findAll()
      .pipe(
        map((apiResponse: ApiResponse<CategoryResponse[]>) => apiResponse.result || []),
        tap(categories => {
          categories.forEach(category => {
            this.initCategoryState(category.id)
            this.loadSubCategories(category.id)
          })
        }),
        catchError(error => {
          console.error("Error fetching Categories: ", error)
          return of([])
        })
      )
  }

  initCategoryState(categoryId: string) {
    this.categoryStates[categoryId] = new BehaviorSubject<CategoryState>({
      subCategories: [],
      currentPage: 0,
      isLoading: false,
      noMoreData: false
    });
  }

  loadSubCategories(categoryId: string) {
    const state = this.categoryStates[categoryId].value
    if (state.isLoading || state.noMoreData) return;

    this.updateCategoryState(categoryId, { isLoading: true })
    this.subCategoryApiService.findByCategoryId(categoryId, state.currentPage, this.subCategoryItemsPerPage)
      .pipe(
        map((apiResponse: ApiResponse<SubCategoryResponse[]>) => apiResponse.result || []),
        catchError(error => {
          console.error("Error fetching Sub Categories: ", error)
          return of([])
        })
      )
      .subscribe(newSubCategoryList => {
        const updatedState = this.categoryStates[categoryId].value
        const allSubCategories = [...updatedState.subCategories, ...newSubCategoryList]
        const noMoreSubCategoriesData = newSubCategoryList.length < this.subCategoryItemsPerPage

        this.updateCategoryState(categoryId, {
          subCategories: allSubCategories,
          currentPage: updatedState.currentPage + 1,
          isLoading: false,
          noMoreData: noMoreSubCategoriesData
        })
      })
  }

  updateCategoryState(categoryId: string, newState: Partial<CategoryState>) {
    this.categoryStates[categoryId].next({
      ...this.categoryStates[categoryId].value,
      ...newState
    });
  }

  showMore(categoryId: string) {
    this.loadSubCategories(categoryId);
  }

  getCategoryState(categoryId: string): Observable<CategoryState> {
    return this.categoryStates[categoryId].asObservable();
  }
}