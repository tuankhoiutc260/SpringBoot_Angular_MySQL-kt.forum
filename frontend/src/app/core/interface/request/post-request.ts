export interface PostRequest {
    image?: File | null;
    title?: string;
    content?: string;
    tags?: string[];
}

