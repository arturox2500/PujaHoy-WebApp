import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { UserComponent } from './components/users/users.component';
import { ProductsFormComponent } from './components/products/products-form.component';
import { ProductsListComponent } from './components/products/products-list.component';
import { ProductDetailComponent } from './components/products/products-details.component';

const routes: Routes = [
  { path: 'users', component: UserComponent },
  { path: 'users/:id', component: UserComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'product-form', component: ProductsFormComponent },
  { path: 'your-auctions', component: ProductsListComponent },
  { path: 'your-winning-bids', component: ProductsListComponent },
  { path: 'product/:id/edit', component: ProductsFormComponent},
  { path: 'product/:id', component: ProductDetailComponent}
];

export const routing = RouterModule.forRoot(routes);