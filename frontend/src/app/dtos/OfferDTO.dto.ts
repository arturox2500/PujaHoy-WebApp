import { UserBasicDTO } from "./UserBasic.dto";
import { ProductBasicDto } from "./ProductBasic.dto";

export interface OfferDTO {
    id: number;
    cost: number; 
    hour: Date; 
    user: UserBasicDTO; 
    product: ProductBasicDto;

}