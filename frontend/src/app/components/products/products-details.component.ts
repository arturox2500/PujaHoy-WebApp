import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { productsService } from '../../services/products.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Chart, CategoryScale, LinearScale, LineElement, PointElement, Title, Tooltip, Legend, LineController } from 'chart.js';
import { usersService } from '../../services/users.service';
import { PublicUserDto } from '../../dtos/PublicUser.dto';
import { OfferDTO } from '../../dtos/OfferDTO.dto';

@Component({
  selector: 'app-product-detail',
  templateUrl: './products-details.component.html',
  styleUrls: ['./products-details.component.css']
})
export class ProductDetailComponent implements OnInit {
  @ViewChild('bidsChart') bidsChart: any;
  showChart = true;
  private chart: Chart | undefined;

  ratedProduct: boolean = false;
  rating!: number;
  checkoutProduct: boolean = true;
  checkoutdone: boolean = false;

  errorMessage: string | undefined;

  user: PublicUserDto | undefined;

  productId: number | undefined;
  public userId: number | undefined;
  product: any;

  bidAmount: number | undefined;
  bidMessage: string = '';
  bidValid: boolean = false;

  message: { text: string, type: 'success' | 'error' } | null = null;

  canPlaceABid: boolean = false;
  canEdit: boolean = false;
  canDelete: boolean = false;
  canCheckOut: boolean = false;
  canRate: boolean = false;

  safeMapUrl?: SafeResourceUrl;

  higherOffer: OfferDTO | undefined;

  winningBid: number | undefined;
  winningBidder: string = 'N/A';

  productOffers: any;

  productNotFound: boolean = false;

  index!: boolean;

  constructor(private loginService: LoginService, private userService: usersService, private productsService: productsService, private route: ActivatedRoute, private router: Router, private sanitizer: DomSanitizer) { }


  ngOnInit(): void {
    let userLoaded = false;
    let productLoaded = false;

    this.route.queryParams.subscribe(params => {
      this.index = params['index'] === 'true';
    });

    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      this.productId = idParam ? +idParam : undefined;

      if (this.productId !== undefined) {
        this.productsService.getProductById(this.productId).subscribe(
          (data) => {
            if (!data) {
              this.productNotFound = true;
              return;
            }
            this.product = data;
            this.productOffers = this.product.offers.map((offer: any) => offer.cost).sort((a:number, b:number) => a - b);
            if (this.product?.offers?.length > 0) {
              this.higherOffer = this.product.offers[this.product.offers.length - 1];
            }
            productLoaded = true;

            if (userLoaded && productLoaded) {
              this.selectPermissions();
            }

            this.generateSafeMapUrl(this.product.seller.zipCode);
            this.loadChart();
          },
          (error) => {
            console.error('Error: ', error);
            this.productNotFound = true;
          }
        );
      }
    });

    this.loginService.reqUser().subscribe(
      (user) => {
        this.user = user;
        userLoaded = true;
        if (userLoaded && productLoaded) {
          this.selectPermissions();
        }
      },
      (error) => {
        console.error('Error to load user: ', error);
      }
    );
  }

  placeBid(): void { // places a bid
    this.productsService.postOffer(this.productId, this.bidAmount).subscribe({
      next: (response) => {
        alert('Bid placed successfully!');
        this.higherOffer = response;
        this.productOffers.push(this.higherOffer?.cost);
        this.product.offers.push(response);
        this.loadChart();
      },
      error: (err) => {
        alert('ERROR, Bid is too low');
      }
    });
  }

  deleteProduct() { // deletes the product
    if (this.productId) {
      this.productsService.deleteProduct(this.productId).subscribe(
        () => {
          alert('Product deleted successfully.');
          this.router.navigate(['/']); 
        },
        (error) => {
          alert('Failed to delete the product.');
        }
      );
    }
  }

  private generateSafeMapUrl(zipCode: string): void { //Generate the map URL using the zip code
    const rawUrl = `https://www.google.com/maps?q=${encodeURIComponent(zipCode + ', España')}&output=embed`;
    this.safeMapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
  }

  
  loadChart() { // Generates and updates bid progression chart.
    Chart.register(CategoryScale, LinearScale, LineElement, PointElement, Title, Tooltip, Legend, LineController);
    const labels = this.productOffers.map((_: any, index: number) => `Bid ${index + 1}`);
    const ctx = this.bidsChart.nativeElement.getContext('2d');
  
    if (this.chart) {
      this.chart.data.labels = labels;
      this.chart.data.datasets[0].data = this.productOffers;
      console.log('Product offers:', this.productOffers);
      this.chart.update();
    } else {
      this.chart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'Bid Progression',
            data: this.productOffers,
            borderColor: 'rgb(75, 192, 192)',
            fill: false,
          }]
        },
        options: {
          scales: {
            x: {
              type: 'category',
            },
            y: {
              type: 'linear',
            }
          }
        }
      });
    }
  }
  

  submitRating() { //Submit the rating to the backend
    if (this.rating < 1 || this.rating > 5) {
      window.alert('Rating must be between 1 and 5');
      return;
    } else {
      this.productsService.rateProduct(this.rating, this.product?.id).subscribe(
        () => {
          this.errorMessage = '';
          const goToProduct = confirm('Rating submitted successfully');
          if (goToProduct) {
            this.ratedProduct = false;
            this.canRate = false;
          }
        },
        (error) => {
          window.alert(error.message);
        }
      );
    }
  }

  checkout() { //Change the state's product to "Delivered"
    this.productsService.checkoutProduct("Delivered", this.product?.id).subscribe(
      (data) => {
        console.log(data);
        this.errorMessage = '';
        this.product = data;
        const goToProduct = confirm('Checkout submitted successfully');
        if (goToProduct) {
          this.checkoutProduct = false;
          this.checkoutdone = true;
          this.canRate = true;
        }
      },
      (error) => {
        window.alert(error.message);
      }
    );
  }

  selectPermissions() { //selects what actions the user can perform on a product
    if (this.user) {
      const isAdmin = this.user.rols?.includes('ADMIN');
      const isSeller = this.user.id === this.product.seller?.id;
      const isActive = this.product.state === "In progress";
      const isFinished = this.product.state === "Finished";
      const isDelivered = this.product.state === "Delivered";
      const isBanned = !this.user.active;
      const hasOffers = this.product.offers.length > 0;

      let isBuyer = false;
      if (hasOffers) {
        isBuyer = this.user.id === this.product.offers[this.product.offers.length - 1].user.id;
      }

      // Place bid
      if (!isAdmin && !isSeller && !isBanned && isActive) {
        this.canPlaceABid = true;
      }

      // Delete And Edit
      if ((isAdmin || isSeller) && (isDelivered || !hasOffers)) {
        this.canDelete = true;
        this.canEdit = true;
      }

      // Checkout
      if (isFinished && isBuyer) {
        this.canCheckOut = true;
      }

    }
  }

  goBack() { //redirects user to index
    this.router.navigate(['/']);
  }
}