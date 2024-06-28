export interface Post {
    id?: string;
    title?: string;
    content?: string;
    tags?: string[];
    createdDate?: Date | string;
    createdBy?: string;
    lastModifiedDate?: Date | string;
    lastModifiedBy?: string;
}
