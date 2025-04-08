import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { routing } from "./app.routes";
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [BrowserModule, FormsModule, HttpClientModule, routing, NgModule],
  bootstrap: [AppComponent],
})
export class AppModule  {}