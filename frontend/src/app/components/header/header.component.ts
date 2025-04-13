import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
})
export class HeaderComponent {
  constructor(public loginService: LoginService, private router: Router) {}
  
  logOut() {
    this.loginService.logOut(); // Llama al método de logout
    this.router.navigate(['/']); // Redirige al login después de cerrar sesión
  }
}
