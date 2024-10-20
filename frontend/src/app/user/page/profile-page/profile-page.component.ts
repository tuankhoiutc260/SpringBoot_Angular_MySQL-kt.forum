import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { catchError, map, Observable, of, shareReplay, Subject, switchMap } from 'rxjs';
import { UserResponse } from '../../../api/model/response/user-response';
import { UserApiService } from '../../../api/service/rest-api/user-api.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss',
  providers: [MessageService]
})
export class ProfilePageComponent implements OnInit {
  isProfileActive: boolean = true;
  isAccountActive: boolean = false;
  isInformationActive: boolean = false;

  userId$!: Observable<string>;
  userResponse$!: Observable<UserResponse | null>;

  private destroy$ = new Subject<void>();

  constructor(
    private userApiService: UserApiService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,

  ) { }

  ngOnInit() {
    this.userId$ = this.activatedRoute.params.pipe(
      map(params => params['userId']),
      shareReplay(1)
    );

    this.userResponse$ = this.initUserResponse();
  }

  private initUserResponse(): Observable<UserResponse | null> {
    return this.userId$.pipe(
      switchMap(userId =>
        this.userApiService.getById(userId).pipe(
          catchError(error => {
            this.handleError('Error fetching User details', error);
            return of(null);
          })
        )
      )
    );
  }

  onProfileActiveChange(isActive: boolean) {
    this.isProfileActive = isActive;
  }

  onInformationActiveChange(isActive: boolean) {
    this.isInformationActive = isActive;
  }

  onAccountActiveChange(isActive: boolean) {
    this.isAccountActive = isActive;
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
