import { WebSocketMessage } from "./web-socket-message";

export interface NewCommentMessage extends WebSocketMessage {
    type: 'NEW_COMMENT';
    payload: {
      id: string;
      content: string;
      createdBy: string;
      createdDate: string;
    };
  }