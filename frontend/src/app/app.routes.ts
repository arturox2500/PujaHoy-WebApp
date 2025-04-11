import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { UserComponent } from './components/users/users.component';

const routes: Routes = [
  { path: 'users', component: UserComponent },
  //{ path: 'auth/login', component: LoginComponent },
  { path: '**', redirectTo: 'auth/login' }
];

export const routing = RouterModule.forRoot(routes);