import { PostResponse } from "./post-response";

export interface SubCategoryResponse {
  id: string;
  title: string;
  description: string;
  coverImage: string;
  createdBy: string;
  createdDate: string | Date;
  lastModifiedBy: string;
  lastModifiedDate: string | Date;
  totalPosts: number
}

