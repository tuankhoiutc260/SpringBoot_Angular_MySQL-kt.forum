import { Component, HostListener, Input } from '@angular/core';
import { UserResponse } from '../../../../api/interface/response/user-response';
import { MenuItem } from 'primeng/api';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { PostApiService } from '../../../../api/service/post-api.service';
import { PostResponse } from '../../../../api/interface/response/post-response';
import { ApiResponse } from '../../../../api/interface/response/apiResponse';
import { AuthService } from '../../../../core/service/auth.service';
import { SafeResourceUrl } from '@angular/platform-browser';
import { PostRequest } from '../../../../api/interface/request/post-request';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  @Input() userLoginInfo: UserResponse = {}

  navBarMenuItems: MenuItem[] | undefined;
  userMenuItems: MenuItem[] | undefined;
  private subscription: Subscription = new Subscription();

  constructor(
    private postApiService: PostApiService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.setUserMenuItems(); 
    this.setNavBarMenuItems();
  }

  isUserLoginInfoEmpty(): boolean {
    return !this.userLoginInfo || Object.keys(this.userLoginInfo).length === 0;
  }

  setUserMenuItems() {
    this.userMenuItems = [
      {
        label: 'Documents',
        items: [
          {
            label: 'New Post',
            icon: 'pi pi-plus',
            shortcut: 'Ctrl+N',
            command: ()=>this.openDialogNew()
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
            label: 'My profile',
            icon: 'pi pi-id-card',
            shortcut: 'Ctrl+M',
            command: () => this.router.navigate([`${this.userLoginInfo.userName}`])
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
            command: () => this.logout()
          }
        ]
      },
      {
        separator: true
      }
    ];
    if (this.userLoginInfo.role?.name === 'ADMIN') {
      this.userMenuItems.push({
        label: 'Management website',
        items: [
          {
            label: 'Management Post',
            icon: 'pi pi-file',
            command: () => this.router.navigate(['/management/post'])
          }
        ]
      });
    }
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.ctrlKey && event.key === 'l') {
      this.logout();
    }
    if (event.ctrlKey && event.key === 'm') {
      this.router.navigate([`${this.userLoginInfo.userName}`])
    }
    if (event.ctrlKey && event.key === 'l') {
      this.router.navigate([`${this.userLoginInfo.userName}`])
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

  logout() {
    this.authService.removeToken();
    this.authService.removeCurrentUserName();
    this.router.navigate(['/login']);
  }

  top10Post: PostResponse[] = []

  getAllPosts(): void {
    const sub = this.postApiService.findTop10ByOrderByLikesDesc().subscribe({
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
    this.subscription.add(sub);
  }

  // new post
  newPost: PostResponse = {}; 
  isDialogVisible: boolean = false;  isActiveImage: boolean = false;

  openDialogNew() {
    this.isDialogVisible = true
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}