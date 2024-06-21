import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../config/config';
import { Observable } from 'rxjs';
import { Post } from '../interface/post';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiURL = API_URL + 'api/v1/post'

  constructor(private http: HttpClient) { }

  /**
* Get Post by ID
* @returns Post
* @example
*    this.postService.getPost(id).subscribe({
*      next: (post) => {
*        this.post = post;
*    },
*    error:(error)=>{
*      console.log(error)
*    }
*  })
*/
  getPost(id: string): Observable<Post> {
    const url = `${this.apiURL}/${id}`;
    return this.http.get<Post>(url);
  }

  /**
 * Get all post from backend
 * @returns array of posts
 * @example
 *    this.postService.getPosts().subscribe({
 *      next: (posts: Post[]) => {
 *        this.posts = posts;
 *    },
 *    error:(error)=>{
 *      console.log(error)
 *    }
 *  })
 */
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiURL)
  }

  /**
   * Create post from backend
   * @returns post
   * @example
   *    this.postService.create().subscribe({
   *      next: (id) =>{
   *        this.post.id = id;
   *    },
   *    error:(error)=>{
   *      console.log(error)
   *    }
   *  })
   */
  create(post: Post): Observable<string> {
    return this.http.post<string>(this.apiURL, post)
  }

  /**
   * Update the existing Post
   * @returns post
   * @example
   *    this.postService.update().subscribe({
   *      next: (id) =>{
   *        this.post.id = id;
   *    },
   *    error:(error)=>{
   *      console.log(error)
   *    }
   *  })
   */
  update(id: string, post: Post): Observable<string> {
    const url = `${this.apiURL}/${id}`;
    return this.http.put<string>(url, post);
  }

  /**
 * Delete Post
 * @example
 *    this.postService.deletePost(id).subscribe({
 *      next: () =>{
 *    },
 *    error:(error)=>{
 *      console.log(error)
 *    }
 *  })
 */
  deletePost(id: string): Observable<string> {
    const url = `${this.apiURL}/${id}`;
    return this.http.delete<string>(url);
  }
}
