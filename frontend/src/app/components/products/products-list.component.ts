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
  public userId: number | undefined;
  currentPage: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;
  products: any[] = [];
  pageTitle = ""

  constructor(private loginService: LoginService, private productsService: productsService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.loginService.reqUser().subscribe((user) => {
      this.userId = user.id;
      this.route.url.subscribe((url) => {
        const path = url[0]?.path;
        if (path === 'your-auctions') {
          this.pageTitle="Your Products"
          this.loadProducts();
        } else if (path === 'your-winning-bids') {
          this.pageTitle="Your Winning Bids"
          this.loadWinningBids();
        }
      });
    }, (error) => {
      console.log('Error:', error);
    });
  }

  loadProducts(): void {
    this.productsService.getUserProducts(this.userId, this.currentPage).subscribe(
      (data) => {
        this.products = this.products.concat(data.content); 
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.currentPage = data.pageable.pageNumber;
      },
      (error) => {
        window.alert('Error al cargar los productos: ' + error.message);
      }
    );
  }

  loadWinningBids(): void {
    this.productsService.getWinningBids(this.userId, this.currentPage).subscribe(
      (data) => {
        this.products = this.products.concat(data.content); 
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.currentPage = data.pageable.pageNumber;
      },
      (error) => {
        window.alert('Error al cargar las pujas ganadas: ' + error.message);
      }
    );
  }

  loadMoreProducts(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  indexProduct(): void {
    this.productsService.getProductIndex(this.currentPage).subscribe(
      (data) => {
        this.products = this.products.concat(data.content); 
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.currentPage = data.pageable.pageNumber;
      },
      (error) => {
        window.alert('Error al cargar las pujas ganadas: ' + error.message);
      }
    );
  }

}
