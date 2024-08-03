export interface CommentRequest {
  content?: string;
  postId?: string;
  parentCommentId?: number | null
}
