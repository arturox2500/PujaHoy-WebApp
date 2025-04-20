import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { productsService } from '../../services/products.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Chart, CategoryScale, LinearScale, LineElement, PointElement, Title, Tooltip, Legend, LineController } from 'chart.js';

@Component({
  selector: 'app-product-detail',
  templateUrl: './products-details.component.html',
  //styleUrls: ['./products-details.component.css']
})
export class ProductDetailComponent implements OnInit {
  @ViewChild('bidsChart') bidsChart: any;
  private chart: Chart | undefined;

  ratedProduct: boolean = false;
  rating!: number;
  checkoutProduct: boolean = true;

  errorMessage: string | undefined;

  productId: number | undefined;
  public userId: number | undefined;
  product: any;

  bidAmount: number | undefined;
  bidMessage: string = '';
  bidValid: boolean = false;

  safeMapUrl?: SafeResourceUrl;

  constructor(private loginService: LoginService, private productsService: productsService, private route: ActivatedRoute, private router: Router, private sanitizer: DomSanitizer) { }


  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      this.productId = idParam ? +idParam : undefined;

      if (this.productId !== undefined) {
        this.productsService.getProductById(this.productId).subscribe(
          (data) => {
            console.log('Producto recibido:', data);
            this.product = data;
            this.generateSafeMapUrl(this.product.seller.zipCode);
            this.loadChart()
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



  placeBid(): void {
    if (this.bidAmount !== undefined && this.bidAmount > 3) {
      this.bidMessage = 'Puja realizada correctamente';
      this.bidValid = true;
    } else {
      this.bidMessage = 'Error al pujar. El valor debe ser mayor a 3';
      this.bidValid = false;
    }
  }

  private generateSafeMapUrl(zipCode: string): void {
    const rawUrl = `https://www.google.com/maps?q=${encodeURIComponent(zipCode + ', España')}&output=embed`;
    this.safeMapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
  }

  loadChart() {
    Chart.register(CategoryScale, LinearScale, LineElement, PointElement, Title, Tooltip, Legend, LineController);
    const values = this.product.offers.map((b: any) => b.cost).sort((a: number, b: number) => a - b);
    const labels = this.product.offers.map((_: any, index: number) => `Bid ${index + 1}`);
    const ctx = this.bidsChart.nativeElement.getContext('2d');

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Bid Progression',
          data: values,
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

  submitRating() {
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
          }
        },
        (error) => {
          window.alert(error.message);
        }
      );
    }
  }

  checkout() {
    this.productsService.checkoutProduct("Delivered", this.product?.id).subscribe(
      (data) => {
        this.errorMessage = '';
        this.product = data;
        const goToProduct = confirm('Checkout submitted successfully');
        if (goToProduct) {
          this.checkoutProduct = false;
        }
      },
      (error) => {
        window.alert(error.message);
      }
    );
  }
}
