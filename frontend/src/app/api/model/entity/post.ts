import { Comment } from "./comment";
import { PostLike } from "./post-like";
import { SubCategory } from "./sub-category";

export interface Post {
    id: string;
    title: string;
    content: string;
    subCategory: SubCategory;
    createdBy: string;
    createdDate: Date | string;
    lastModifiedBy: string;
    lastModifiedDate: Date | string;
    postLikes?: PostLike[];
    comments?: Comment[];
    viewCount: number;
}
