import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { productsService } from '../../services/products.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  templateUrl: './products-details.component.html',
  //styleUrls: ['./products-details.component.css']
})
export class ProductDetailComponent implements OnInit {
    productId: number | undefined;
    public userId: number | undefined;
    product: any;
  
    constructor(private loginService: LoginService, private productsService: productsService, private route: ActivatedRoute) {}
  
    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const idParam = params.get('id');
            this.productId = idParam ? +idParam : undefined;
        
            if (this.productId !== undefined) {
              this.productsService.getProductById(this.productId).subscribe(
                (data) => {
                  console.log('Producto recibido:', data);
                  this.product = data;
                },
                (error) => {
                  console.error('Error al cargar el producto:', error);
                }
              );
            } else {
              console.error('Product ID no válido');
            }
          });
        this.loginService.reqUser().subscribe((user) => {
            this.userId = user.id;
        });
        
    }
  }