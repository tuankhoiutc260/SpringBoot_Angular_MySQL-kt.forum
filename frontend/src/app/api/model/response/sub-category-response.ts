export interface SubCategoryResponse {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  cloudinaryImageId: string;
  createdBy: string;
  createdDate: string | Date;
  lastModifiedBy: string;
  lastModifiedDate: string | Date;
  postCount: number;
}
