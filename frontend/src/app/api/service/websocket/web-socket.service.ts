import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { environment } from '../../../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private state: BehaviorSubject<boolean>;

  constructor() {
    this.client = new Client({
      brokerURL: environment.webSocketUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.state = new BehaviorSubject<boolean>(false);

    this.client.onConnect = () => {
      console.log('Connected to WebSocket');
      this.state.next(true);
    };

    this.client.onDisconnect = () => {
      console.log('Disconnected from WebSocket');
      this.state.next(false);
    };

    this.client.activate();
  }

  public connect(): Observable<boolean> {
    return this.state.asObservable();
  }

  public listen(destination: string): Observable<any> {
    return new Observable(observer => {
      if (!this.client.connected) {
        observer.error(new Error('STOMP client is not connected'));
        return;
      }

      const subscription = this.client.subscribe(destination, message => {
        observer.next(JSON.parse(message.body));
      });

      return () => {
        subscription.unsubscribe();
      };
    });
  }

  public send(destination: string, body: any): void {
    if (!this.client.connected) {
      console.error('STOMP client is not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body)
    });
  }
}