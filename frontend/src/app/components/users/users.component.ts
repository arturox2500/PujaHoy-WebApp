import { Component, OnInit } from '@angular/core';
import { PublicUserDto } from '../../dtos/PublicUser.dto';
import { usersService } from '../../services/users.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html'
})
export class UserComponent implements OnInit {
    user: PublicUserDto | undefined;
    errorMessage: string | undefined;
  
    constructor(private usersService: usersService) {}
  
    ngOnInit() {
        this.usersService.getProfile().subscribe(
          (response) => {
            this.user = response;
            this.errorMessage = ''; 
          },
          (error) => {
            this.errorMessage = error;
            console.error('Error loading user profile:', error);
          }
        );
      }
}