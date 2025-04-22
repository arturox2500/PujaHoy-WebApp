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

  editProduct(product: CreateProductDto, prodId: number | undefined) {
    if (prodId === undefined) {
      throw new Error('User ID is undefined');
    }
    return this.http.put(`/api/v1/products/${prodId}`, product , { withCredentials: true })
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

  putImage(productId: number, formData: FormData) {
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

  checkoutProduct(state: string, id: number) {
    return this.http.put(`/api/v1/products/${id}`, { state }, { withCredentials: true })
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

  getProductById(prodId: number | undefined): Observable<any> {
    if (prodId === undefined) {
      throw new Error('Product ID is undefined');
    }
    return this.http.get<any>(`/api/v1/products/${prodId}`)
        .pipe(
          catchError(this.handleError)
        );
  }

  postOffer(productId: number | undefined, cost: number | undefined): Observable<any> {
    if (productId === undefined) {
      throw new Error('Product ID is undefined');
    }
    const url = `/api/v1/products/${productId}/offers`;
    const body = { cost };
  
    return this.http.post<any>(url, body)
      .pipe(
        catchError(this.handleError)
      );
  }

  deleteProduct(prodId: number | undefined): Observable<any> {
    return this.http.delete(`/api/v1/products/${prodId}`);
  }

  getProductIndex(page: number): Observable<any> {
    return this.http.get<any>(/api/v1/products?page=${page})
        .pipe(
          catchError(this.handleError)
        );
  }  

  private handleError(error: HttpErrorResponse) {
    if (error.error && error.error.error) {
      console.error(`Backend error: ${error.error.error}`);
      return throwError(() => new Error(error.error.error));
    } else if (error.status === 400) {
      console.error('Error 400: Bad request');
      return throwError(() => new Error('Bad request, please check your input'));
    } else if (error.status === 401) {
      console.error('Error 401: Unauthorized access');
      return throwError(() => new Error('Unauthorized access, please log in again'));
    } else if (error.status === 403) {
      console.error('Error 403: Forbidden access');
      return throwError(() => new Error('Forbidden access, you do not have permission to view this resource'));
    } else if (error.status === 404) {
      console.error('Error 404: User not found');
      return throwError(() => new Error('User not found'));
    } else if (error.status === 500) {
      console.error('Error 500: Internal error');
      return throwError(() => new Error('Server error, please try again later'));
    } else if (error.status === 0) {
      console.error('Network error: Unable to connect to the server');
      return throwError(() => new Error('Unable to connect to the server'));
    } else {
      console.error(`Unexpected error: ${error.message}`);
      return throwError(() => new Error('Unexpected error'));
    }
  }
    
}