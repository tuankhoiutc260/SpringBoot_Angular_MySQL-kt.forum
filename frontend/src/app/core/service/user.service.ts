import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { Observable } from 'rxjs';
import { User } from '../interface/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiURL = API_URL + 'api/v1/user'

  constructor(private http: HttpClient) { }

  /**
   * Get all user from backend
   * @returns array of users
   * @example
   *    this.userService.getUsers().subscribe({
   *      next: (users: User[]) => {
   *        this.users = users;
   *    },
   *    error:(error)=>{
   *      console.log(error)
   *    }
   *  })
   */

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiURL)
  }

  /**
   * Create user from backend
   * @returns user
   * @example
   *    this.userService.create().subscribe({
   *      next: (id) =>{
   *        this.user.id = id;
   *    },
   *    error:(error)=>{
   *      console.log(error)
   *    }
   *  })
   */
  create(user: User): Observable<string> {
    return this.http.post<string>(this.apiURL, user)
  }

  /**
   * Update the existing User
   * @returns user
   * @example
   *    this.userService.update().subscribe({
   *      next: (id) =>{
   *        this.user.id = id;
   *    },
   *    error:(error)=>{
   *      console.log(error)
   *    }
   *  })
   */
  update(id: string, user: User): Observable<string> {
    const url = `${this.apiURL}/${id}`;
    return this.http.put<string>(url, user);
  }

  /**
 * Get specified user from backend
 * @returns a user
 * @example
 *    this.userService.getUser().subscribe({
 *      next: (user) =>{
 *        this.user = user
 *    },
 *    error:(error)=>{
 *      console.log(error)
 *    }
 *  })
 */
  getUser(id: string): Observable<User> {
    const url = `${this.apiURL}/${id}`;
    return this.http.get<User>(url);
  }
}
