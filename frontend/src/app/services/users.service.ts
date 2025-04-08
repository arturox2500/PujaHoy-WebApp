import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { PublicUserDto } from "../dtos/PublicUser.dto";

@Injectable({ providedIn: "root" })
export class usersService {
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
}