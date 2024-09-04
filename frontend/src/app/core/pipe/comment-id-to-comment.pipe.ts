import { Pipe, PipeTransform } from '@angular/core';
import { CommentApiService } from '../../api/service/rest-api/comment-api.service';
import { CommentResponse } from '../../api/model/response/comment-response';
import { map, Observable } from 'rxjs';

@Pipe({
  name: 'commentIdToComment'
})
export class CommentIdToCommentPipe implements PipeTransform {

  constructor(private commentApiService: CommentApiService) { }

  transform(commentId: number): Observable<CommentResponse | undefined> {
    return this.commentApiService.getById(commentId).pipe(
      map(commentResponse => commentResponse)
    );
  }
}
