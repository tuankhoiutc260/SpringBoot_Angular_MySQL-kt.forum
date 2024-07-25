import { Injectable } from '@angular/core';
import { RxStomp } from '@stomp/rx-stomp';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketMessage } from '../model/entity/web-socket-message';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private rxStomp: RxStomp;
  private commentSubjects: Map<string, BehaviorSubject<WebSocketMessage | null>> = new Map();

  constructor() {
    this.rxStomp = new RxStomp();
    this.rxStomp.configure({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
    });
    this.rxStomp.activate();
  }

  public getCommentUpdates(postId: string): Observable<WebSocketMessage | null> {
    if (!this.commentSubjects.has(postId)) {
      const subject = new BehaviorSubject<WebSocketMessage | null>(null);
      this.commentSubjects.set(postId, subject);

      this.rxStomp.watch(`/topic/comments/${postId}`).subscribe((message) => {
        subject.next(JSON.parse(message.body));
      });
    }
    return this.commentSubjects.get(postId)!.asObservable();
  }

  public sendComment(comment: any): void {
    this.rxStomp.publish({
      destination: '/app/comment',
      body: JSON.stringify(comment)
    });
  }
}