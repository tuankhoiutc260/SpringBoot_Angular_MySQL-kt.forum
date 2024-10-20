import { Component, OnInit } from '@angular/core';
import { UserApiService } from '../../../api/service/rest-api/user-api.service';
import { ActivatedRoute } from '@angular/router';
import { MenuItem, MessageService, PrimeIcons } from 'primeng/api';
import { BehaviorSubject, catchError, map, Observable, of, shareReplay, Subject, switchMap, take, takeUntil, tap, throwError } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { alphanumericValidator } from '../../../core/validator/alphanumeric.validator';
import { passwordMatchValidator } from '../../../core/validator/password-match-validator.validator';
import { UserRequest } from '../../../api/model/request/user-request';
import { SafeResourceUrl } from '@angular/platform-browser';
import { UserResponse } from '../../../api/model/response/user-response';

@Component({
  selector: 'app-profile1',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  






  // constructor(
  //   private fb: FormBuilder,
  //   private userApiService: UserApiService,
  //   private activatedRoute: ActivatedRoute,
  //   private messageService: MessageService,
  // ) {
  //   this.userProfileForm = this.initUserProfileForm();
  //   this.userAccountForm = this.initUserAccountForm();
  // }









  






}

