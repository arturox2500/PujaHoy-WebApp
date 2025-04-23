import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { UserDto } from '../../dtos/User.dto';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  public isRegister: boolean = false; 
  public userDto: UserDto = { username: '', password: '', email: '', zipCode: 0, visibleName: '' }; 
  public errorMessage: string | null = null; 

  constructor(private loginService: LoginService, private router: Router) {}

  ngOnInit() { // Check if the user is already logged in and redirect to users page if true
    if (this.loginService.isLogged()) {
      this.router.navigate(['/users']); 
    }
  }
  
  toggleForm() { // Toggle between login and registration forms
    this.isRegister = !this.isRegister;
    this.errorMessage = null; 
  }

  onSubmit() { // Handle form submission for both login and registration
    if (this.isRegister) {
      this.loginService.register(this.userDto).subscribe(
        (response) => {
          console.log('Registration successful:', response);
          this.isRegister = false; 
          this.errorMessage = null;
          this.router.navigate(['/']);          
        },
        (error) => {
          console.error('Registration failed:', error);
          this.errorMessage =
            error.error?.error || 'Registration failed. Please try again.';
        }
      );
    } else {
      
      this.loginService.logIn(this.userDto.username, this.userDto.password).subscribe(
        (response) => {
          console.log('Login successful:', response);
          this.errorMessage = null;
          this.loginService.setLogged(true); 
          this.router.navigate(['/']);
        },
        (error) => {
          console.error('Login failed:', error);
          this.errorMessage = 'Invalid username or password.';
        }
      );
    }
  }
}