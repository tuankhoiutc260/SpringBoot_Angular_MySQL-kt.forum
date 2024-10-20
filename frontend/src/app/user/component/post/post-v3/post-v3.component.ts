import { Component, Input, OnInit } from '@angular/core';
import { PostResponse } from '../../../../api/model/response/post-response';
import { combineLatest, map, Observable } from 'rxjs';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { UserApiService } from '../../../../api/service/rest-api/user-api.service';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { UserResponse } from '../../../../api/model/response/user-response';

@Component({
  selector: 'app-post-v3',
  templateUrl: './post-v3.component.html',
  styleUrl: './post-v3.component.scss'
})
export class PostV3Component implements OnInit {
  @Input() postResponse!: PostResponse;

  combinedData$!: Observable<{
    category: CategoryResponse;
    subCategory: SubCategoryResponse;
    user: UserResponse;
  }>;

  constructor(
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService,
    private userApiService: UserApiService
  ) {}

  ngOnInit() {
    this.combinedData$ = combineLatest([
      this.categoryApiService.getBySubCategoryId(this.postResponse.subCategoryId),
      this.subCategoryApiService.getById(this.postResponse.subCategoryId),
      this.userApiService.getById(this.postResponse.createdBy)
    ]).pipe(
      map(([category, subCategory, user]) => ({
        category,
        subCategory,
        user
      }))
    );
  }
}
