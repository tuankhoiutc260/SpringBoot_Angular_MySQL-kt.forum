import { Pipe, PipeTransform } from '@angular/core';
import { CategoryResponse } from '../../api/model/response/category-response';
import { map, Observable } from 'rxjs';
import { CategoryApiService } from '../../api/service/rest-api/category-api.service';

@Pipe({
  name: 'subCategoryIdToCategory'
})
export class SubCategoryIdToCategoryPipe implements PipeTransform {

  constructor(private categoryApiService: CategoryApiService) { }

  transform(subCategoryId: string): Observable<CategoryResponse | undefined> {
    return this.categoryApiService.getBySubCategoryId(subCategoryId).pipe(
      map(categoryResponse => categoryResponse)
    );
  }
}
