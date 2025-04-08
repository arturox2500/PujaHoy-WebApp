import { Component } from '@angular/core';
//import { LoginService } from '../../services/login.service';
//import { UserDto } from '../../dtos/User.dto';

@Component({
  selector: 'app-login',
  template: '<h1>Hola mundo</h1>'//./login.component.html',
  //styleUrls: ['./login.component.css'],
})
export class LoginComponent {/*
  public isRegister: boolean = false; // Alternar entre login y registro
  public userDto: UserDto = { username: '', password: '', email: '', zipCode: '', visibleName: '' }; // DTO para login/registro
  public errorMessage: string | null = null; // Mensaje de error

  constructor(private loginService: LoginService) {}

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
        },
        (error) => {
          console.error('Login failed:', error);
          this.errorMessage = 'Invalid username or password.';
        }
      );
    }
  }*/
}