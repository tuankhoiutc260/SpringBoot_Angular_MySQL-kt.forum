export interface PostResponse {
    id: string;
    title: string;
    description: string;
    content: string;
    subCategoryId: string;
    tags: string[];
    createdDate: Date | string;
    createdBy: string;
    lastModifiedDate: Date | string;
    lastModifiedBy: string;
    likeCount: number;
    commentCount: number;
    viewCount: number;
}
