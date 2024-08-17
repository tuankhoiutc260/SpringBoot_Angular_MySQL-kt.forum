import { CommentResponse } from "./comment-response";
import { LikeResponse } from "./like-response";

export interface PostResponse {
    id: string;
    // image?:string;
    title: string;
    content: string;
    tags: string[];
    createdDate: Date | string;
    createdBy: string;
    lastModifiedDate: Date | string;
    lastModifiedBy: string;
    likes?: LikeResponse[]
    countLikes?: number
    totalComments: number;
    // comments?: CommentResponse[]
    viewCount: number
}
