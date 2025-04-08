import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  //{ path: 'auth/login', component: LoginComponent },
  { path: '**', redirectTo: 'auth/login' }
];

export const routing = RouterModule.forRoot(routes);