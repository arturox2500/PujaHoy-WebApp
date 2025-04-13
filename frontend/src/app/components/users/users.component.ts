import { Component } from '@angular/core';
import { PublicUserDto } from '../../dtos/PublicUser.dto';
import { ActivatedRoute } from '@angular/router';
import { usersService } from '../../services/users.service';
import { LoginService } from '../../services/login.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html'
})
export class UserComponent {
  userId: number | null = null;
  user: PublicUserDto | undefined;
  errorMessage: string | undefined;
  typeApplication: string | undefined;
  text!: string;
  applicater!: PublicUserDto;

  constructor(private route: ActivatedRoute, private usersService: usersService, private loginService: LoginService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.userId = id ? +id : null;
      this.getRolCurrentUser();

      if (this.userId) {
        this.getSellerProfile(this.userId);
      } else {
        this.getOwnProfile();
      }
    });
  }

  getRolCurrentUser() {
    this.usersService.getProfile().subscribe(
      (response) => {
        this.applicater = response;
      },
      (error) => {
        this.errorMessage = error;
      }
    );
  }

  getOwnProfile() {
    this.usersService.getProfile().subscribe(
      (response) => {
        this.user = response;
        this.typeApplication = 'owner';
        console.log(this.typeApplication);
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
    forkJoin({
      applicater: this.usersService.getProfile(),
      user: this.usersService.getSellerProfile(id)
    }).subscribe(
      ({ applicater, user }) => {
        this.applicater = applicater;
        this.user = user;

        if (this.applicater.rols.includes('ADMIN')) {
          this.typeApplication = 'admin';
        } else if (this.applicater.id === this.user?.id) {
          this.typeApplication = 'owner';
        } else {
          this.typeApplication = 'other';
        }

        console.log(this.typeApplication);
        this.text = this.user.active ? 'Ban User' : 'Unban User';
        this.errorMessage = '';
      },
      (error) => {
        this.errorMessage = error;
      }
    );
  }

  bannedUser(id: number) {
    this.usersService.bannedUser(id).subscribe(
      () => {
        if (this.text === 'Ban User') {
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