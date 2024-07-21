import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AuthService } from '../core/service/auth.service';
import { UserResponse } from '../api/model/response/user-response';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss',
  providers: [MessageService]
})

export class UserComponent implements OnInit, OnDestroy {
  @ViewChild('header', { static: false }) header!: ElementRef;
  defaultUserLoginInfo: UserResponse = {};
  userLoginInfo: UserResponse | null = null;
  private subscription: Subscription = new Subscription();
  userName: string = '';
  canEdit: boolean = false;

  constructor(
    private authService: AuthService,
  ) { }

  ngOnInit() {
    this.getUserLoginInfo();
  }

  ngAfterViewInit() {
    this.adjustContentMargin();
  }

  getUserLoginInfo() {
    this.authService.fetchAndSetUserLoginInfo();
    this.subscription.add(
      this.authService.getUserLoginInfo().subscribe(userLoginInfo => {
        this.userLoginInfo = userLoginInfo;
        if (this.userName === this.userLoginInfo?.userName) {
          this.canEdit = true;
        }
      })
    );
  }

  adjustContentMargin() {
    if (this.header && this.header.nativeElement) {
      const headerHeight = this.header.nativeElement.offsetHeight;
      const content = document.querySelector('.content') as HTMLElement;
      if (content) {
        content.style.marginTop = `${headerHeight}px`;
      }
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
