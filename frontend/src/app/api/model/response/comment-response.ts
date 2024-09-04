export interface CommentResponse {
  id: number;
  content: string;
  parentId: number;
  postId: string;
  createdBy: string;
  createdDate: Date | string;
  lastModifiedDate: Date | string;
}
