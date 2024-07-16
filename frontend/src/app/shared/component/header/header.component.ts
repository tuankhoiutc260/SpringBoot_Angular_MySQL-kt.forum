import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { PostService } from '../../../core/service/post.service';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { PostResponse } from '../../../core/interface/response/post-response';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../../core/service/auth.service';
import { Router } from '@angular/router';
import { UserService } from '../../../core/service/user.service';
import { UserResponse } from '../../../core/interface/response/user-response';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  navBarMenuItems: MenuItem[] | undefined;
  currentUserName: string = '';
  userMenuItems: MenuItem[] | undefined;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {

  }

  ngOnInit(): void {

    this.getCurrentUserName();

    this.onGetInfoUser();

    this.setUserMenuItems();

    this.setNavBarMenuItems();
  }

  setUserMenuItems() {
    this.userMenuItems = [
      {
        label: 'Documents',
        items: [
          {
            label: 'New',
            icon: 'pi pi-plus',
            shortcut: '⌘+N'
          },
          {
            label: 'Search',
            icon: 'pi pi-search',
            shortcut: '⌘+S',
          }
        ]
      },
      {
        label: 'Profile',
        items: [
          {
            label: 'Settings',
            icon: 'pi pi-cog',
            shortcut: '⌘+O'
          },
          {
            label: 'Messages',
            icon: 'pi pi-inbox',
            badge: '2'
          },
          {
            label: 'Logout',
            icon: 'pi pi-sign-out',
            shortcut: 'Ctrl+L',
            command: ()=> this.logout()
          }
        ]
      },

      {
        separator: true
      }
    ];
  }
  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === 'l') {
      this.logout();
    }
  }
  
  setNavBarMenuItems() {
    this.navBarMenuItems = [
      {
        label: 'Home',
        icon: 'pi pi-home'
      },
      {
        label: 'Topic',
        icon: 'pi pi-star',
        items: [
          {
            label: 'Core',
            icon: 'pi pi-bolt',
          },
          {
            label: 'Blocks',
            icon: 'pi pi-server',
          },
          {
            label: 'UI Kit',
            icon: 'pi pi-pencil',
          },
         
        ]
      },
      {
        label: 'Notification',
        icon: 'pi pi-bell',
      }
    ];
  }

  activateSignUp(value: boolean) {
    this.authService.setSignUpActive(value);
  }

  getCurrentUserName() {
    this.currentUserName = this.authService.getCurrentUserName();
  }

  logout() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();
    this.router.navigate(['/login']);
  }
  
  userResponse: UserResponse = {}

  onGetInfoUser() {
    this.userService.findByUserName(this.currentUserName).subscribe({
      next: (apiResponse: ApiResponse<UserResponse>) => {
        this.userResponse = apiResponse.result!

      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        console.error(httpErrorResponse)
      }
    })
  }

  top10Post: PostResponse[] = []

  getAllPosts(): void {
    this.postService.findTop10ByOrderByLikesDesc().subscribe({
      next: (apiResponse: ApiResponse<PostResponse[]>) => {
        const postResponseList = apiResponse.result;
        if (postResponseList) {
          this.top10Post = postResponseList;
        } else {
          console.error('No result found in response:', apiResponse.message);
        }
      },
      error: (error) => {
        console.error('Error fetching posts:', error);
      }
    });
  }
  
}
