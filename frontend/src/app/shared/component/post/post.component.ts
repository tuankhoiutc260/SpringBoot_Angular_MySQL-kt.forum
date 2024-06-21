import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Post } from '../../../core/interface/post';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent {
  @Input() post!: Post;
  @Input() canEdit!: boolean;
  @Output() isEditing = new EventEmitter();
}
