import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { productsService } from '../../services/products.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-list',  
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.css']
})

export class ProductsListComponent implements OnInit {
  public userId: number | undefined = this.loginService.currentUser()?.id;
  products: any[] = [];

  constructor(private loginService: LoginService, private productsService: productsService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    // Verifica si la URL es '/your-auctions'
    this.route.url.subscribe((url) => {
      if (url[0].path === 'your-auctions') {
        this.productsService.getUserProducts(this.userId).subscribe(
          (data) => {
            this.products = data;
            console.log(data);
          },
          (error) => {
            window.alert('Error al cargar los productos: ' + error.message);
          }
        );
      } else if (url[0].path === 'your-winning-bids'){
        this.productsService.getWinningBids(this.userId).subscribe(
          (data) => {
            this.products = data;
            console.log(data);
          },
          (error) => {
            window.alert('Error al cargar los productos: ' + error.message);
          }
        );
      }
    });
  }

}