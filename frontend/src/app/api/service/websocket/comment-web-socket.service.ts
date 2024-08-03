import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { CommentResponse } from '../../model/response/comment-response';
import { BehaviorSubject, buffer } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentWebSocketService {
  private client: Client
  private commentSubject = new BehaviorSubject<CommentResponse | null > (null)
  constructor() { 
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: ()=>{
        console.log('Connect to WebSocket')
        this.client.subscribe('/topic/comments/*', (message)=>{
          const comment: CommentResponse = JSON.parse(message.body)
          this.commentSubject.next(comment)
        })
      }
    });
    this.client.activate()
  }

  public getCommentSubject(){
    return this.commentSubject.asObservable()
  }
}
