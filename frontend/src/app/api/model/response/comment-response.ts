export interface CommentResponse {
  id?: number;
  content?: string;
  postId?: string;
  parentId?: number;
  replies?: CommentResponse[]
  createdBy?: string;
  createdDate?: Date | string;
  lastModifiedDate?: Date | string;
}

