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
    this.loginService.logOut(); 
    this.router.navigate(['/']);
  }
}
