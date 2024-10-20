import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom, Observable, Subject } from 'rxjs';
import { UserApiService } from '../../api/service/rest-api/user-api.service';
import { AuthApiService } from '../../api/service/rest-api/auth-api.service';
import { StorageService } from './storage.service';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isSignUpActiveSource = new BehaviorSubject<boolean>(false);
  isSignUpActive$ = this.isSignUpActiveSource.asObservable();

  private currentUserIdSubject = new BehaviorSubject<string | null>(null);
  currentUserId$ = this.currentUserIdSubject.asObservable();

  isAuthenticated$: Observable<boolean>;

  constructor(
    private userApiService: UserApiService,
    private authApiService: AuthApiService,
    private storageService: StorageService,
  ) {
    this.isAuthenticated$ = this.isAuthenticated();
    this.initCurrentUserId();
  }

  setSignUpActive(value: boolean) {
    this.isSignUpActiveSource.next(value);
  }

  getSignUpActive() {
    return this.isSignUpActiveSource.getValue();
  }

  async setCurrentUserId(username: string): Promise<void> {
    try {
      const userResponse = await firstValueFrom(this.userApiService.getByUserName(username));
      const userId = userResponse.id;
      this.storageService.setItem("user_id", String(userId));
    } catch (error: any) {
      console.error('Error fetching User information:', error);
      throw new Error(error.message || 'Server error');
    }
  }

  private initCurrentUserId() {
    const storedUserId = this.getCurrentUserId();
    if (storedUserId) {
      this.currentUserIdSubject.next(storedUserId);
    }
  }

  getCurrentUserId(): string | null {
    return this.storageService.getItem('user_id');
  }

  isAuthenticated() {
    return this.authApiService.isAuthenticated();
  }
}
