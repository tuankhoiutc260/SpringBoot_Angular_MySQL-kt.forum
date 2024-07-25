import { Post } from "./post";

export interface Comment {
    id?: string;
    content?:string;
    createdDate?: Date | string;
    createdBy?: string;
    post?: Post
}
