export interface CategoryResponse {
  id: string;
  title: string;
  description: string;
  createdBy: string;
  createdDate: string | Date;
  lastModifiedBy: string;
  lastModifiedDate: string | Date;
}
