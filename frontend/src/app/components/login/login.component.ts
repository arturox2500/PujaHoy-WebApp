import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { UserDto } from '../../dtos/User.dto';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  //styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  public isRegister: boolean = false; // Alternar entre login y registro
  public userDto: UserDto = { username: '', password: '', email: '', zipCode: '', visibleName: '' }; // DTO para login/registro
  public errorMessage: string | null = null; // Mensaje de error

  constructor(private loginService: LoginService, private router: Router) {}

  ngOnInit() {
    // Verificar si el usuario ya está logueado
    if (this.loginService.isLogged()) {
      this.router.navigate(['/users']); // Redirigir a la página de usuarios si ya está logueado
    }
  }
  
  toggleForm() {
    this.isRegister = !this.isRegister;
    this.errorMessage = null; // Limpiar mensaje de error al alternar
  }

  onSubmit() {
    if (this.isRegister) {
      // Registro
      this.loginService.register(this.userDto).subscribe(
        (response) => {
          console.log('Registration successful:', response);
          this.isRegister = false; // Cambiar a login después del registro
          this.errorMessage = null;
          this.router.navigate(['/users']);
        },
        (error) => {
          console.error('Registration failed:', error);
          this.errorMessage =
            error.error?.error || 'Registration failed. Please try again.';
        }
      );
    } else {
      // Login
      this.loginService.logIn(this.userDto.username, this.userDto.password).subscribe(
        (response) => {
          console.log('Login successful:', response);
          this.errorMessage = null;
          this.router.navigate(['/users']);
        },
        (error) => {
          console.error('Login failed:', error);
          this.errorMessage = 'Invalid username or password.';
        }
      );
    }
  }
}