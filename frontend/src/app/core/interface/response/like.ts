import { PostResponse } from "./post-response"
import { UserResponse } from "./user-response"

export interface LikeResponse {
    id?: number,
    createDate?: Date | string,
    user?: UserResponse,
    post?: PostResponse
}
