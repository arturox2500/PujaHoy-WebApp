import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CreateProductDto } from "../dtos/CreateProduct.dto";
import { catchError, map, Observable, throwError } from "rxjs";
import { ProductBasicDto } from "../dtos/ProductBasic.dto";

@Injectable({ providedIn: "root" })
export class productsService {
  private apiUrl = 'https://localhost:8443/api/v1/products'
  constructor(private http: HttpClient) { }

  createProduct(product: CreateProductDto) {
    return this.http.post('/api/v1/products', product , { withCredentials: true })
          .pipe(
            catchError(this.handleError)
          );
  }

  uploadImage(productId: number, formData: FormData) {
    return this.http.post(`/api/v1/products/${productId}/image`, formData, {withCredentials: true, responseType: "text"})
          .pipe(
            catchError(this.handleError)
        );
  }

  rateProduct(rating: number, id: number) {
    return this.http.post(`/api/v1/products/${id}/ratings`, { rating }, { withCredentials: true })
          .pipe(
            catchError(this.handleError)
          );
  }

  getUserProducts(userId: number | undefined, page: number): Observable<any> {
    if (userId === undefined) {
      throw new Error('User ID is undefined');
    }
    return this.http.get<any>(`/api/v1/users/${userId}/products?page=${page}`)
        .pipe(
          catchError(this.handleError)
        );
  }

  getWinningBids(userId: number | undefined, page: number): Observable<any> {
    if (userId === undefined) {
      throw new Error('User ID is undefined');
    }
    return this.http.get<any>(`/api/v1/users/${userId}/boughtProducts?page=${page}`)
        .pipe(
          catchError(this.handleError)
        );
  }

  private handleError(error: HttpErrorResponse) {
      if (error.status === 400) {
          console.error('Error 400: Bad request');
          return throwError('Bad request, please check your input');
      } else if (error.status === 401) {
        console.error('Error 401: Unauthorized access');
        return throwError('Unauthorized access, please log in again');
      } else if (error.status === 403) {
        console.error('Error 403: Forbidden access');
        return throwError('Forbidden access, you do not have permission to view this resource');
      } else if (error.status === 404) {
        console.error('Error 404: User not found');
        return throwError('User not found');
      } else if (error.status === 500) {
        console.error('Error 500: Internal error');
        return throwError('Server error, please try again later');
      } else if (error.status === 0) {
        console.error('Network error: Unable to connect to the server');
        return throwError('Unable to connect to the server');
      } else {
        console.error(`Unexpected error: ${error.message}`);
        return throwError('Unexpected error');
      }
    }
    
}