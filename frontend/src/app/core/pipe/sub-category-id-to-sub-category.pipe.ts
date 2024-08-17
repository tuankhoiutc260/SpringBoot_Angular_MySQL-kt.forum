import { Pipe, PipeTransform } from '@angular/core';
import { SubCategoryApiService } from '../../api/service/rest-api/sub-category-api.service';
import { map, Observable } from 'rxjs';
import { SubCategoryResponse } from '../../api/model/response/sub-category-response';

@Pipe({
  name: 'subCategoryIdToSubCategory'
})
export class SubCategoryIdToSubCategoryPipe implements PipeTransform {

  constructor(private subCategoryApiService: SubCategoryApiService) { }

  transform(subCategoryId: string): Observable<SubCategoryResponse | undefined> {
    return this.subCategoryApiService.findById(subCategoryId).pipe(
      map(apiResponse => apiResponse.result)
    );
  }
}
