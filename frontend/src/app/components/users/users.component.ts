import { Component} from '@angular/core';
import { PublicUserDto } from '../../dtos/PublicUser.dto';
import { ActivatedRoute } from '@angular/router';
import { usersService } from '../../services/users.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html'
})
export class UserComponent{
    userId: number | null = null;
    user: PublicUserDto | undefined;
    errorMessage: string | undefined;
    text!: string;
  
    constructor(private route: ActivatedRoute, private usersService: usersService) {}

    ngOnInit(): void {
      this.route.paramMap.subscribe((params) => {
        const id = params.get('id');
        this.userId = id ? +id : null;
  
        if (this.userId) {
          this.getSellerProfile(this.userId); 
        } else {
          this.getOwnProfile();
        }
      });
    }

    getOwnProfile() {
      this.usersService.getProfile().subscribe(
        (response) => {
          this.user = response;
          if (this.user.active)
            this.text = 'Ban User';
          else
            this.text = 'Unban User'; 
        },
        (error) => {
          this.errorMessage = error;
        }
      );
    }
    
    getSellerProfile(id: number) {
        this.usersService.getSellerProfile(id).subscribe(
          (response) => {
            this.user = response;
            if (this.user.active)
              this.text = 'Ban User';
            else
              this.text = 'Unban User'; 
            this.errorMessage = ''; 
          },
          (error) => {
            this.errorMessage = error;
          }
        );
      }
    
    bannedUser(id: number){
      this.usersService.bannedUser(id).subscribe(
        () => {
          if (this.text === 'Ban User'){
            this.text = 'Unban User';
            window.alert("User banned successfully");
          } else {
            this.text = 'Ban User';
            window.alert("User unbanned successfully");
          }
          this.errorMessage = ''; 
        },
        (error) => {
          this.errorMessage = error;
        }
      );
    }
}