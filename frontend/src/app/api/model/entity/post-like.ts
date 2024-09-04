import { Post } from "./post"
import { User } from "./user"

export interface PostLike {
    id: number,
    createDate: Date | string,
    user: User,
    post: Post
}
