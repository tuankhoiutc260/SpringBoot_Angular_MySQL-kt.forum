export interface PostResponse {
    id: string;
    title: string;
    content: string;
    subCategoryId: string;
    createdDate: Date | string;
    createdBy: string;
    lastModifiedDate: Date | string;
    lastModifiedBy: string;
    likeCount: number;
    commentCount: number;
    viewCount: number;
}
