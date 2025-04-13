import { Component, OnInit } from '@angular/core';
import { productsService } from '../../services/products.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-products-form',  
  templateUrl: './products-form.component.html',
  styleUrls: ['./products-form.component.css']
})

export class ProductsFormComponent implements OnInit {
  errorMessage: string | undefined;
  constructor(private productsService: productsService, private router: Router) { }

  ngOnInit(): void {
  }

  product = {
    name: '',
    description: '',
    iniValue: 0,
    duration: 7,
  };

  image=null

  submitForm() {
    const productToSend = {
      name: this.product.name,
      description: this.product.description,
      iniValue: this.product.iniValue,
      duration: this.product.duration
    };
  
    this.productsService.createProduct(productToSend).subscribe(
      (createdProduct: any) => {
        this.errorMessage = '';
  
        if (this.image) {
          const formData = new FormData();
          formData.append('image', this.image);
  
          this.productsService.uploadImage(createdProduct.id, formData).subscribe(
            () => {
              this.router.navigate(['/']);
            },
            (error) => {
              window.alert('Producto creado, pero falló la subida de imagen: ' + error.message);
            }
          );
        } else {
          this.router.navigate(['/']);
        }
      },
      (error) => {
        window.alert('Error al crear el producto: ' + error.message);
      }
    );
  }


  onImageSelected(event: any) {
    const file = event.target.files[0];
    this.image = file;
    console.log('Image selected:', file);
  }

}