import { Component } from '@angular/core';
import { PublicUserDto } from '../../dtos/PublicUser.dto';
import { ActivatedRoute } from '@angular/router';
import { usersService } from '../../services/users.service';
import { catchError, forkJoin, of } from 'rxjs';

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
  applicater: PublicUserDto | undefined;

  constructor(private route: ActivatedRoute, private usersService: usersService) { }

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
        if (this.user.rols?.includes('ADMIN')) {
          this.errorMessage = "Error, admins can't have a profile";
          this.user = undefined;
        } else {
          this.typeApplication = 'owner';
          console.log(this.typeApplication);
          if (this.user.active)
            this.text = 'Ban User';
          else
            this.text = 'Unban User';
          this.errorMessage = '';
        }
      },
      (error) => {
        this.errorMessage = error;
      }
    );
  }

  getSellerProfile(id: number) {
    forkJoin({
      applicater: this.usersService.getProfile().pipe(
        catchError(() => of(undefined))
      ),
      user: this.usersService.getSellerProfile(id)
    }).subscribe(({ applicater, user }) => {
      this.applicater = applicater || undefined;
      this.user = user;
      console.log(this.applicater?.rols);
      if (applicater?.rols?.includes('ADMIN')) {
        this.typeApplication = 'admin';
      } else if (applicater?.id === user.id) {
        this.typeApplication = 'owner';
      } else {
        this.typeApplication = 'other';
      }
      this.errorMessage = '';
      this.text = user.active ? 'Ban User' : 'Unban User';
    });
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