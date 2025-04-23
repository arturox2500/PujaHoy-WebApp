import { Component } from '@angular/core';
import { PublicUserDto } from '../../dtos/PublicUser.dto';
import { ActivatedRoute } from '@angular/router';
import { usersService } from '../../services/users.service';
import { catchError, forkJoin, of } from 'rxjs';
import { UserEditDto } from '../../dtos/UserEdit.dto';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UserComponent {
  userId: number | null = null;
  user: PublicUserDto | undefined;
  errorMessage: string | undefined;
  typeApplication: string | undefined;
  text!: string;
  applicater: PublicUserDto | undefined;
  editData: UserEditDto = { id: 0, zipCode: 0, contact: '', description: '' };
  isEditing: boolean = false;
  selectedImage: File | null = null;
  imageUrl: string | undefined;

  constructor(private route: ActivatedRoute, private usersService: usersService) { }

  ngOnInit(): void { //Call the proper methods depending on the route parameter
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

  getOwnProfile() { //If the user is logged in, get their profile
    this.usersService.getProfile().subscribe(
      (response) => {
        this.user = response;
        if (this.user.rols?.includes('ADMIN')) {
          this.errorMessage = "Error, admins can't have a profile";
          this.user = undefined;
        } else if (this.user.active === false) {
          this.errorMessage = "You are banned, please contact the administrator";
          this.user = undefined;
        } else {
          this.typeApplication = 'owner';
          console.log(this.typeApplication);
          if (this.user.active)
            this.text = 'Ban User';
          else
            this.text = 'Unban User';
          this.errorMessage = '';

          this.editData = {
            id: this.user.id,
            zipCode: this.user.zipCode || 0,
            contact: this.user.contact || '',
            description: this.user.description || '',
          };
          if (this.user?.image) {
            this.imageUrl = `/api/v1${this.user.image}?t=${Date.now()}`;
          }
        }
      },
      (error) => {
        this.errorMessage = error;
      }
    );
  }

  getSellerProfile(id: number) { //If you get the seller profile
    forkJoin({
      applicater: this.usersService.getProfile().pipe(
        catchError(() => of(undefined))
      ),
      user: this.usersService.getSellerProfile(id)
    }).subscribe(({ applicater, user }) => {
      this.applicater = applicater || undefined;
      this.user = user;
      if (this.user?.id === this.applicater?.id) {
        this.getOwnProfile();
        return;
      }
      if (this.user.rols?.includes('ADMIN')) {
        this.errorMessage = "Error, admins can't have a profile";
        this.user = undefined;
        return;
      }
      console.log(this.applicater?.rols);
      if (applicater?.rols?.includes('ADMIN')) {
        this.typeApplication = 'admin';
      } else if (applicater?.id === user.id) {
        this.typeApplication = 'owner';
        if (this.user.active === false) {
          this.errorMessage = "You are banned, please contact the administrator";
          this.user = undefined;
          return;
        }
      } else {
        this.typeApplication = 'other';
      }
      this.errorMessage = '';
      this.text = user.active ? 'Ban User' : 'Unban User';
      if (this.user?.image) {
        this.imageUrl = `/api/v1${this.user.image}?t=${Date.now()}`;
      }
    });
  }

  bannedUser(id: number) { //Change the active's attribute of the user
    this.usersService.bannedUser(id).subscribe(
      (data) => {
        if (this.text === 'Ban User') {
          this.text = 'Unban User';
          window.alert("User banned successfully");
        } else {
          this.text = 'Ban User';
          window.alert("User unbanned successfully");
        }
        this.user = data;
        this.errorMessage = '';
      },
      (error) => {
        this.errorMessage = error;
      }
    );
  }

  onSubmitEdit() { //Edit user profile
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const zipCodeRegex = /^\d{5}$/;

    if (!emailRegex.test(this.editData.contact)) {
      alert('Invalid email format. Please enter a valid email address.');
      return;
    }

    if (!zipCodeRegex.test(this.editData.zipCode.toString())) {
      alert('Invalid ZIP code. Please enter a 5-digit ZIP code.');
      return;
    }

    this.usersService.updateProfile(this.editData).subscribe(
      (response) => {
        console.log('Profile updated successfully:', response);
        alert('Profile updated successfully!');
        if (this.selectedImage && this.user?.id) {
          this.usersService.uploadUserImage(this.user?.id, this.selectedImage).subscribe(
            (response) => {
              console.log('Imagen subida correctamente.');
              this.selectedImage = null;
              this.imageUrl = `/api/v1${this.user?.image}?t=${Date.now()}`;
            },
            (err) => {
              console.error('Error al subir la imagen:', err);
              alert('Error al subir la imagen.');
            }
          );
        }

        if (this.user) {
          this.user.zipCode = this.editData.zipCode ?? 0;
          this.user.contact = this.editData.contact || '';
          this.user.description = this.editData.description || '';
          this.isEditing = false;
        }
      },
      (error) => {
        console.error('Error updating profile:', error);
        const errorMessage = error?.error?.message || 'Failed to update profile';
        alert(`Error: ${errorMessage}`);
      }
    );
  }

  cancelEdit() { //Cancel the edit and reset the form
    if (this.user) {
      this.editData = {
        id: this.user.id,
        zipCode: this.user.zipCode ? +this.user.zipCode : 0,
        contact: this.user.contact || '',
        description: this.user.description || '',
      };
    }
    this.isEditing = false;
  }

  onImageSelected(event: Event) { //Handle the image selection
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
      console.log('Image:', this.selectedImage);
    }
  }

}