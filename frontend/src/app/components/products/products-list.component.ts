import { Component, OnDestroy, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { productsService } from '../../services/products.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-product-list',
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.css']
})

export class ProductsListComponent implements OnInit, OnDestroy{
  private logoutSubscription: Subscription | undefined;

  public userId: number | undefined;
  currentPage: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;
  products: any[] = [];
  pageTitle = ""

  constructor(private loginService: LoginService, private productsService: productsService, private route: ActivatedRoute) { }

  ngOnInit(): void {   //Initializes the component by fetching the current user and loading the appropriate product list based on the active route.
    this.logoutSubscription = this.loginService.logout$.subscribe(() => {
      this.reloadComponent();
    });

    this.loginService.reqUser().subscribe((user) => {
      this.userId = user.id;
      this.route.url.subscribe((url) => {
        const fullPath = url.map(segment => segment.path).join('/');
  
        if (!fullPath) {
          this.pageTitle = "";
          this.indexProduct();
        } else if (fullPath === 'your-auctions') {
          this.pageTitle = "Your Products";
          this.loadProducts();
        } else if (fullPath === 'your-winning-bids') {
          this.pageTitle = "Your Winning Bids";
          this.loadWinningBids();
        }
      });
    }, (error) => {
      console.log('Error:', error);
      this.pageTitle = "";
      this.indexProduct();
    });
  }

  reloadComponent(): void {  // Reload the component when logout is triggered
    this.currentPage = 0;
    this.products = [];
    this.ngOnInit(); 
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

  loadMoreProducts(): void { //Loads the next page of products based on the current route and appends them to the existing list.
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;

      this.route.url.subscribe((url) => {
        const fullPath = url.map(segment => segment.path).join('/');
  
        if (!fullPath) {
          this.pageTitle = "";
          this.indexProduct();
        } else if (fullPath === 'your-auctions') {
          this.pageTitle = "Your Products";
          this.loadProducts();
        } else if (fullPath === 'your-winning-bids') {
          this.pageTitle = "Your Winning Bids";
          this.loadWinningBids();
        }
      });
    }
  }

  indexProduct(): void { // Loads a general list of products (not user-specific) for the current page and appends them to the list.
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

  ngOnDestroy(): void {
    this.logoutSubscription?.unsubscribe(); // Clean the subcription
  }
}
