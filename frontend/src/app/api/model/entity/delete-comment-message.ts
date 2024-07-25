import { WebSocketMessage } from "./web-socket-message";

export interface DeleteCommentMessage extends WebSocketMessage {
    type: 'DELETE_COMMENT';
    payload: string; // commentId
  }