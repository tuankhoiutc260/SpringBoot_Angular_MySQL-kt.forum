import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { CommentResponse } from '../../model/response/comment-response';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private client: Client;
  private commentSubject = new BehaviorSubject<CommentResponse | null>(null);
  private connectPromise: Promise<void>;

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
      },
      onWebSocketClose: () => {
        console.warn('WebSocket connection closed');
      }
    });

    this.connectPromise = new Promise<void>((resolve) => {
      this.client.onConnect = () => {
        console.log('Connected to WebSocket');
        resolve();
      };
    });

    this.client.activate();
  }

  async subscribeToComments(postId: string): Promise<void> {
    try {
      await this.connectPromise;
      this.client.subscribe(`/topic/comments/${postId}`, (message: IMessage) => {
        // console.log('Received message:', message.body);
        const comment: CommentResponse = JSON.parse(message.body);
        this.commentSubject.next(comment);
      });
      console.log(`Subscribed to /topic/comments/${postId}`);
    } catch (error) {
      console.error('Error subscribing to comments:', error);
    }
  }

  getCommentObservable(): Observable<CommentResponse | null> {
    return this.commentSubject.asObservable();
  }
}

// import { Injectable } from '@angular/core';
// import { RxStomp } from '@stomp/rx-stomp';
// import { BehaviorSubject, Observable } from 'rxjs';
// import { WebSocketMessage } from '../../model/entity/web-socket-message';

// @Injectable({
//   providedIn: 'root'
// })
// export class WebSocketService {
//   private rxStomp: RxStomp;
//   private commentSubjects: Map<string, BehaviorSubject<WebSocketMessage | null>> = new Map();

//   constructor() {
//     this.rxStomp = new RxStomp();
//     this.rxStomp.configure({
//       brokerURL: 'ws://localhost:8080/ws',
//       reconnectDelay: 5000,
//     });
//     this.rxStomp.activate();
//   }

//   public getCommentUpdates(postId: string): Observable<WebSocketMessage | null> {
//     if (!this.commentSubjects.has(postId)) {
//       const subject = new BehaviorSubject<WebSocketMessage | null>(null);
//       this.commentSubjects.set(postId, subject);

//       this.rxStomp.watch(`/topic/comments/${postId}`).subscribe((message) => {
//         subject.next(JSON.parse(message.body));
//       });
//     }
//     return this.commentSubjects.get(postId)!.asObservable();
//   }

//   public sendComment(comment: any): void {
//     this.rxStomp.publish({
//       destination: '/app/comment',
//       body: JSON.stringify(comment)
//     });
//   }
// }