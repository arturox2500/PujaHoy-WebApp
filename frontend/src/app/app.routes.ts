import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { UserComponent } from './components/users/users.component';
import { ProductsFormComponent } from './components/products/products-form.component';
import { ProductsListComponent } from './components/products/products-list.component';
import { RatedComponent } from './components/rated/rated.component';

const routes: Routes = [
  { path: 'users', component: UserComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'product-form', component: ProductsFormComponent },
  { path: 'your-auctions', component: ProductsListComponent },
  { path: 'your-winning-bids', component: ProductsListComponent },
  { path: 'products/:id/rated', component: RatedComponent }
];

export const routing = RouterModule.forRoot(routes);