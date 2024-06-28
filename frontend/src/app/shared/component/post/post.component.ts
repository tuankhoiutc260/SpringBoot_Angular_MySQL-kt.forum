import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Post } from '../../../core/interface/model/post';
import { PostResponse } from '../../../core/interface/response/post-response';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent {
  @Input() postResponse!: PostResponse;
  @Input() canEdit!: boolean;
  @Output() isEditing = new EventEmitter();
}
