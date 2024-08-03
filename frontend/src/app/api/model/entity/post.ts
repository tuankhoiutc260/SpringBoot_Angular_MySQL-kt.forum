import { Comment } from "./comment";
import { Like } from "./like";

export interface Post {
    id?: string;
    image?: string;
    title?: string;
    content?: string;
    tags?: string[];
    createdBy?: string;
    createdDate?: Date | string;
    lastModifiedBy?: string;
    lastModifiedDate?: Date | string;
    likes?: Like[];
    comments?: Comment[]
}
