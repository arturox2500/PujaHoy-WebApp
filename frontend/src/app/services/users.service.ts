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

  public getProfile(): Observable<PublicUserDto> {
    return this.http.get<PublicUserDto>('/api/v1/users', { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  public getSellerProfile(id: number): Observable<PublicUserDto> {
    return this.http.get<PublicUserDto>(`/api/v1/users/${id}`, { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  public bannedUser(id: number) {
    return this.http.put(`/api/v1/users/${id}/active`, { withCredentials: true })
      .pipe(
        catchError(this.handleError)
      );
  }

  getAuthenticatedUser(): Observable<PublicUserDto> {
    return this.http.get<PublicUserDto>(`/api/v1/me`); // Endpoint para el usuario autenticado
  }


  private handleError(error: HttpErrorResponse) {
    if (error.status === 400) {
        console.error('Error 400: Bad request');
        return throwError('Bad request, please check your input');
    } else if (error.status === 401) {
      console.error('Error 401: Unauthorized access');
      return throwError('Unauthorized access, please log in again');
    } else if (error.status === 403) {
      console.error('Error 403: Forbidden access');
      return throwError('Forbidden access, you do not have permission to view this resource');
    } else if (error.status === 404) {
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