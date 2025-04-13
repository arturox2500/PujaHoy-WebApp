import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { productsService } from '../../services/products.service';

@Component({
  selector: 'app-product-rating',
  templateUrl: './rated.component.html'
})
export class RatedComponent {
    rating!: number;
    id!: number;
    errorMessage: string | undefined;

    constructor(private productsService: productsService, private router: Router) {}

    submitRating(rating: number, id: number) {
        this.productsService.rateProduct(rating, id).subscribe(
            () => {
              this.errorMessage = ''; 
              const goToProduct = confirm('Rating submitted successfully');
              if (goToProduct) {
                  this.router.navigate(['/']); //`/products/${id}`]); FALTA CREAR LA PÁGINA DE PRODUCTOS
              }
            },
            (error) => {
                window.alert('Error rating product: ' + error.message);
            }
        );

    }
}