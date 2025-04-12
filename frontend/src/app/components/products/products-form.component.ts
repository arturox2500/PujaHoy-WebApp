import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-products-form',  
  templateUrl: './products-form.component.html',
  styleUrls: ['./products-form.component.css']
})

export class ProductsFormComponent implements OnInit {

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
  }

  product = {
    name: '',
    description: '',
    iniValue: 0,
    duration: 7,
    image: null
  };

  submitForm() {
    const productToSend = {
      name: this.product.name,
      description: this.product.description,
      iniValue: this.product.iniValue,
      duration: this.product.duration
    };

    this.http.post('https://localhost:8443/api/v1/user/submit_auction', productToSend)
      .subscribe({
        next: (res) => {
          console.log('Producto creado:', res);
        },
        error: (err) => {
          console.error('Error al crear producto:', err);
        }
      });
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    this.product.image = file;
    console.log('Image selected:', file);
  }

}