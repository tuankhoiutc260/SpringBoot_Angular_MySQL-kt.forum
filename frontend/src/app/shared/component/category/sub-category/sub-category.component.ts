import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';

@Component({
  selector: 'app-sub-category',
  templateUrl: './sub-category.component.html',
  styleUrl: './sub-category.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubCategoryComponent {
  @Input() subCategoryResponse!: SubCategoryResponse;
}