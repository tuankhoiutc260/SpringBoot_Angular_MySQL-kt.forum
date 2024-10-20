import { Component, ElementRef, HostListener, OnDestroy, OnInit, Renderer2, ViewChild } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { catchError, Observable, Subject, takeUntil, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/service/auth.service';
import { AuthApiService } from '../../../../api/service/rest-api/auth-api.service';
import { Menu } from 'primeng/menu';
import { PostResponse } from '../../../../api/model/response/post-response';
import { StorageService } from '../../../../core/service/storage.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  @ViewChild('userMenu') userMenuOverlayPanel!: Menu;
  @ViewChild('userMenuTrigger', { static: false }) userMenuTrigger!: ElementRef;
  
  newPost!: PostResponse;

  currentUserId: string | null = '';

  isAuthenticated$: Observable<boolean> | undefined;
  isAuthenticatedStatus: boolean = false;

  navbarItems: MenuItem[] | undefined;
  userMenuItems: MenuItem[] | undefined;

  isDialogVisible = false;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private authApiService: AuthApiService,
    private router: Router,
    private messageService: MessageService,
    private renderer: Renderer2
  ) { }

  ngOnInit() {
    this.currentUserId = this.authService.getCurrentUserId();
    this.setNavbarItems();
    this.setUserMenuItems();
    this.subscribeToAuthStatus();    
  }

  subscribeToAuthStatus() {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.isAuthenticated$
      .pipe(takeUntil(this.destroy$))
      .subscribe((status) => {
        this.isAuthenticatedStatus = status;
      });
  }

  setNavbarItems(){
    this.navbarItems = [
      {
        label: 'Home',
        icon: 'pi pi-home'
      },
      {
        label: 'Features',
        icon: 'pi pi-star'
      },
      {
        label: 'Contact',
        icon: 'pi pi-envelope',
        badge: '3'
      }
    ];
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
            command: () => this.openDialogNew(),
          },
          {
            label: 'Search',
            icon: 'pi pi-search',
            shortcut: '⌘+S',
            // command: () => this.searchAction(),
          },
        ],
      },
      {
        label: 'Profile',
        items: [
          {
            label: 'My Profile',
            icon: 'pi pi-id-card',
            shortcut: 'Ctrl+M',
            command: () => this.router.navigate(['/user/' + this.currentUserId]),
          },
          {
            label: 'Messages',
            icon: 'pi pi-inbox',
            // badge: `${this.userMessagesCount}`,
            command: () => this.router.navigate(['/messages']),
          },
          {
            label: 'Logout',
            icon: 'pi pi-sign-out',
            shortcut: 'Ctrl+L',
            command: () => this.logout(),
          },
        ],
      },
      {
        separator: true,
      },
    ];
  }

  toggleUserMenu(event: Event) {
    this.userMenuOverlayPanel.toggle(event);
    this.setUserMenuPosition();
  }

  setUserMenuPosition() {
    if (!this.userMenuTrigger) return; 

    const triggerPos = this.userMenuTrigger.nativeElement.getBoundingClientRect();
    const overlayElement = document.querySelector('.user-menu-custom') as HTMLElement;

    if (overlayElement) {
      this.renderer.setStyle(overlayElement, 'top', `${triggerPos.bottom + window.scrollY}px`);
      this.renderer.setStyle(overlayElement, 'right', `1rem`); 
    }
  }

  @HostListener('window:scroll', [])
  onWindowScroll(): void {
    if (this.userMenuOverlayPanel.overlayVisible) {
      this.setUserMenuPosition();
    }
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if (event.ctrlKey && event.key.toLowerCase() === 'l') {
      this.logout();
    }
    if (event.ctrlKey && event.key.toLowerCase() === 'm') {
      this.router.navigate(['/user/']);
    }
  }

  logout(): void {
    this.authApiService
      .logout()
      .pipe(
        tap(() => {
          this.storageService.removeItem('user_id');
          this.router.navigate(['/login']);
        }),
        catchError((error) => {
          console.error('Error during logout:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Logout error',
          });
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  openDialogNew(): void {
    this.isDialogVisible = true;
  }

  activateSignUp(value: boolean): void {
    this.authService.setSignUpActive(value);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
