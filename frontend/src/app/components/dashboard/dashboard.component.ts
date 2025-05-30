import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { WebsocketsService } from '../../services/websockets.service';
import { Subscription } from 'rxjs';
import { WebsocketMessage } from '../../../types';
import { ChatService } from '../../services/chat.service';
import { NgClass } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './dashboard.component.html',
  standalone: true,
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  requestChatForm = new FormGroup({
    targetUsername: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  requestedChat: string | null = null;
  chatRequests: string[] = [];
  private messageSubscription!: Subscription;

  constructor(
    private websocketsService: WebsocketsService,
    private chatService: ChatService,
    private toastrService: ToastrService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.messageSubscription = this.websocketsService
      .getMessages()
      .subscribe((message) => {
        this.handleWebsocketMessage(message);
      });
    this.updateChatRequests();
  }

  handleWebsocketMessage(message: WebsocketMessage) {
    switch (message.type) {
      case 'request': {
        this.updateChatRequests();
        break;
      }
      case 'cancel': {
        this.updateChatRequests();
        break;
      }
      case 'accept': {
        this.startChat(message.payload);
        break;
      }
    }
  }

  updateChatRequests() {
    this.chatService.getChatRequests().subscribe({
      next: (requests) => {
        this.chatRequests = requests;
      },
      error: () => {
        this.toastrService.error(
          'Something went wrong while fetching chats requested from you',
        );
      },
    });
  }

  requestChat() {
    const requestedChat =
      this.requestChatForm.controls.targetUsername.getRawValue();
    this.chatService.requestChat(this.requestChatForm.getRawValue()).subscribe({
      next: (response) => {
        this.toastrService.success(response);
        this.requestedChat = requestedChat;
      },
      error: (err) => {
        this.toastrService.error(err.error);
      },
    });
  }

  cancelChatRequests() {
    this.chatService.cancelChatRequests().subscribe({
      next: (response) => {
        this.toastrService.success(response);
        this.requestedChat = null;
      },
      error: () => {
        this.toastrService.error(
          'Something went wrong while canceling chat requests',
        );
      },
    });
  }

  acceptChatRequest(username: string) {
    this.chatService.acceptChat({ targetUsername: username }).subscribe({
      next: () => {
        this.startChat(username);
      },
      error: (err) => {
        this.toastrService.error(err.error);
      },
    });
  }

  startChat(username: string) {
    this.router.navigate(['/chat', username]);
  }
}
