import { Component } from "@angular/core";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { WebsocketsService } from "../../services/websockets.service";
import { webSocket } from "rxjs/webSocket";

@Component({
  selector: "app-dashboard",
  imports: [ReactiveFormsModule],
  templateUrl: "./dashboard.component.html",
  standalone: true,
  styleUrl: "./dashboard.component.scss",
})
export class DashboardComponent {
  requestChatForm = new FormGroup({
    targetUsername: new FormControl<string>("", {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  constructor(private websocketsService: WebsocketsService) {}
}
