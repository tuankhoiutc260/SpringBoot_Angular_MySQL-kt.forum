export interface PostResponse {
    id?: string;
    image?:string;
    // image?:File;
    title?: string;
    content?: string;
    tags?: string[];
    createdDate?: Date | string;
    createdBy?: string;
    lastModifiedDate?: Date | string;
    lastModifiedBy?: string;
}


// export interface PostResponse {
//     id: string;
//     image?: string;
//     title: string;
//     content: string;
//     tags: string[];
//     createdDate: Date;
//     createdBy: string;
//     lastModifiedDate: Date;
//     lastModifiedBy: string;
//   }
  