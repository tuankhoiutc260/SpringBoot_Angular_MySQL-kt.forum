export interface CommentRequest {
  content: string;
  parentCommentId: number | null;
  postId: string;
}
