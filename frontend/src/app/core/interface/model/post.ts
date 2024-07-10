export interface Post {
    id?: string;
    // image?:Uint8Array;
    image?:string;
    title?: string;
    content?: string;
    tags?: string[];
    createdDate?: Date | string;
    createdBy?: string;
    lastModifiedDate?: Date | string;
    lastModifiedBy?: string;
}
