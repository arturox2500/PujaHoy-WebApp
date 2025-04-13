import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { productsService } from '../../services/products.service';
import { ProductBasicDto } from '../../dtos/ProductBasic.dto';

@Component({
  selector: 'app-product-list',  
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.css']
})

export class ProductsListComponent implements OnInit {
  public userId: number | undefined = this.loginService.currentUser()?.id;
  products: ProductBasicDto[] = [];

  constructor(private loginService: LoginService, private productsService: productsService) { }

  ngOnInit(): void {
    this.productsService.getUserProducts(this.userId).subscribe(
      (data) => {
        this.products = data;
        console.log(data)
      },
      (error) => {
        window.alert('Error al cargar los productos: ' + error.message);
      }
    );
  }

}