export interface SubCategoryRequest {
    title: string;
    description: string;
    image?: File | null;
    categoryId: string;
}
