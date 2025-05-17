import { Injectable } from "@angular/core";
import { webSocket, WebSocketSubject } from "rxjs/webSocket";
import { Observable } from "rxjs";
import {WebsocketMessage} from '../../types';

@Injectable({
  providedIn: "root",
})
export class WebsocketsService {
  private socket$: WebSocketSubject<WebsocketMessage>;

  constructor() {
    this.socket$ = webSocket("ws://localhost:8080/sockets/chatrequests");
  }

  getMessages(): Observable<WebsocketMessage> {
    return this.socket$.asObservable();
  }

  closeConnection() {
    this.socket$.complete();
  }
}
