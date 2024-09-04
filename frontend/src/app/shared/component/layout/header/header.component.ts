import { Component, HostListener, Input, OnDestroy, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { catchError, Subject, takeUntil, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/service/auth.service';
import { PostResponse } from '../../../../api/model/response/post-response';
import { UserResponse } from '../../../../api/model/response/user-response';
import { AuthApiService } from '../../../../api/service/rest-api/auth-api.service';
import { LogoutRequest } from '../../../../api/model/request/logout-request';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Input() userLoginInfo!: UserResponse;

  navBarMenuItems: MenuItem[] | undefined;
  userMenuItems: MenuItem[] | undefined;
  newPost!: PostResponse

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private authApiService: AuthApiService,
    private router: Router,
    private messageService: MessageService,

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
            command: () => this.openDialogNew()
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
    if (this.userLoginInfo?.role?.name === 'ADMIN') {
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
  isDialogVisible = false;

  openDialogNew() {
    this.isDialogVisible = true
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

  logout() {
    const logoutRequest: LogoutRequest = { token: this.authService.getToken() };

    this.authApiService.logout(logoutRequest)
      .pipe(
        tap(() => {
          this.authService.removeToken();
          this.authService.removeCurrentUserName();
          this.router.navigate(['/login']);
        }),
        catchError(error => {
          console.error('Error during logout:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Logout error' });
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      ).subscribe()
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
