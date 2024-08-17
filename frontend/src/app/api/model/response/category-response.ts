import { SubCategoryResponse } from "./sub-category-response";

export interface CategoryResponse {
  id: string;
  title: string;
  description: string;
  // subCategories: SubCategoryResponse[];
  createdBy: string;
  createdDate: string | Date;
  lastModifiedBy: string;
  lastModifiedDate: string | Date;
}