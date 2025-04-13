import { NgModule } from '@angular/core';
import { routing } from "./app.routes";
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { UserComponent } from './components/users/users.component';
import { LoginComponent } from './components/login/login.component';
import { ProductsFormComponent } from './components/products/products-form.component';
import { RatedComponent } from './components/rated/rated.component';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    UserComponent,
    ProductsFormComponent,
    RatedComponent
  ],
  imports: [BrowserModule, FormsModule, HttpClientModule, routing],
  bootstrap: [AppComponent]
})
export class AppModule  {}