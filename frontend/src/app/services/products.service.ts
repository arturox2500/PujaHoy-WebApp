import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CreateProductDto } from "../dtos/CreateProduct.dto";

@Injectable({ providedIn: "root" })
export class productsService {
    private apiUrl = 'https://localhost:8443/api/v1/products'
    constructor(private http: HttpClient) {}

    createProduct(product: CreateProductDto) {
        return this.http.post(this.apiUrl, product);
      }

}