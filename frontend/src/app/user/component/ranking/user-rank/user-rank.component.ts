import { Component } from '@angular/core';
import { MessageService } from 'primeng/api';
import { catchError, Observable, of, Subject } from 'rxjs';
import { UserRankResponse } from '../../../../api/model/response/user-rank-response';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';

@Component({
  selector: 'app-user-rank',
  templateUrl: './user-rank.component.html',
  styleUrl: './user-rank.component.scss',
  providers: [MessageService],
})
export class UserRankComponent {
  userRankResponseList$!: Observable<UserRankResponse[]>;

  constructor(
    private postApiService: PostApiService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void {
    this.getTopTrendingPosts();
  }

  getTopTrendingPosts() {
    this.userRankResponseList$ = this.postApiService.getTop3UserRanks()
      .pipe(
        catchError(error => {
          this.handleError('Error loading the Top Users list', error);
          return of([]);
        })
      );
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }
}
