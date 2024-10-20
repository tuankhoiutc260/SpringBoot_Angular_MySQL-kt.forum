import { Component } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { MessageService } from 'primeng/api';
import { SubCategoryRankResponse } from '../../../../api/model/response/sub-category-rank-response';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';

@Component({
  selector: 'app-sub-category-rank',
  templateUrl: './sub-category-rank.component.html',
  styleUrl: './sub-category-rank.component.scss',
  providers: [MessageService],
})
export class SubCategoryRankComponent {
  subCategoryRankResponseList$!: Observable<SubCategoryRankResponse[]>;

  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.getTopTrendingPosts();
  }

  getTopTrendingPosts() {
    this.subCategoryRankResponseList$ = this.postApiService.getTheTop10MostSubCategory()
      .pipe(
        catchError(error => {
          this.handleError('Error loading the Top Topics list', error);
          return of([])
        })
      );
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }
}
