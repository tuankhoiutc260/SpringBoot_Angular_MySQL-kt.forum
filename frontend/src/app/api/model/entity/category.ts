import { SubCategory } from "./sub-category";

export interface Category {
    id: string;
    title: string;
    description: string;
    subCategories: SubCategory[]
    createdBy: string | Date;
    createdDate: string | Date
    lastModifiedBy: string;
    lastModifiedDate: string;
}
