import { Post } from "./post";

export interface Comment {
    id: number;
    content: string;
    parentComment?: Comment
    post: Post
    replies?: Comment[]
    createdBy: string;
    createdDate: Date | string;
    lastModifiedDate: Date | string
}
