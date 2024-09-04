import { Category } from "./category";
import { Post } from "./post";

export interface SubCategory {
    id: string;
    title: string;
    description: string;
    imageUrl: string;
    cloudinaryImageId: string;
    category: Category
    posts: Post[]
    createdBy: string | Date
    createdDate: string | Date
    lastModifiedBy: string;
    lastModifiedDate: string;
}
