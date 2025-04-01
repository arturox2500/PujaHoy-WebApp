import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { UserDto } from "../dtos/User.dto";
import { PublicUserDto } from "../dtos/PublicUser.dto";

const BASE_URL = "/api/v1/auth";

@Injectable({ providedIn: "root" })
export class LoginService {
  public logged: boolean | undefined;
  public user: PublicUserDto | undefined;

  constructor(private http: HttpClient) {
    this.reqIsLogged();
  }

  public reqIsLogged() {
    this.http.get("/api/v1/users", { withCredentials: true }).subscribe(
      (response) => {
        this.user = response as PublicUserDto;
        this.logged = true;
      },
      (error) => {
        if (error.status != 404) {
          console.error(
            "Error when asking if logged: " + JSON.stringify(error)
          );
        }
      }
    );
  }

  public logIn(user: string, pass: string) {
    return this.http.post(
      BASE_URL + "/login",
      { username: user, password: pass },
      { withCredentials: true }
    );
  }

  public logOut() {
    return this.http
      .post(BASE_URL + "/logout", { withCredentials: true })
      .subscribe((_) => {
        console.log("LOGOUT: Successfully");
        this.logged = false;
        this.user = undefined;
      });
  }

  public isLogged() {
    return this.logged;
  }

  public isAdmin() {
    return this.user && this.user.roles.indexOf("ADMIN") !== -1;
  }

  currentUser() {
    return this.user;
  }
}