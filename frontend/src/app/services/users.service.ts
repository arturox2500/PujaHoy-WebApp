import { Injectable } from "@angular/core";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { PublicUserDto } from "../dtos/PublicUser.dto";
import {Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({ providedIn: "root" })
export class usersService {

  public user: PublicUserDto | undefined;
  constructor(private http: HttpClient) {}

  public getUsers() {
    return this.http.get<PublicUserDto[]>("/api/v1/users", { withCredentials: true });
  }

  public getUser(id: string) {
    return this.http.get<PublicUserDto>(`/api/v1/users/${id}`, { withCredentials: true });
  }

  public updateUser(userDto: PublicUserDto) {
    return this.http.put(`/api/v1/users/${userDto.id}`, userDto, { withCredentials: true });
  }

  public deleteUser(id: string) {
    return this.http.delete(`/api/v1/users/${id}`, { withCredentials: true });
  }

  public getProfile(): Observable<PublicUserDto> { //Falta hacer q devulva el usuario logueado
    return this.http.get<PublicUserDto>('/api/v1/users/7', { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status === 404) {
      console.error('Error 404: User not found');
      return throwError('User not found');
    } else if (error.status === 500) {
      console.error('Error 500: Internal error');
      return throwError('Server error, please try again later');
    } else if (error.status === 0) {
      console.error('Network error: Unable to connect to the server');
      return throwError('Unable to connect to the server');
    } else {
      console.error(`Unexpected error: ${error.message}`);
      return throwError('Unexpected error');
    }
  }
}