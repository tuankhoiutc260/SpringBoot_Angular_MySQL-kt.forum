import { Component, Input, OnInit } from '@angular/core';
import { PostResponse } from '../../../../api/model/response/post-response';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss'] // Sửa 'styleUrl' thành 'styleUrls'
})
export class PostComponent implements OnInit {
  @Input() postResponse!: PostResponse;
  minRead: number = 0;

  ngOnInit(): void {
    if (this.postResponse && this.postResponse.content) {
      this.minRead = this.calculateMinRead(this.postResponse.content);
    }
  }

  calculateMinRead(text: string): number {
    const wordsPerMinute = 200;
    const cleanText = text.replace(/[^\w\s]/gi, '');
    const textLength = cleanText.split(/\s+/).length;
    return Math.ceil(textLength / wordsPerMinute);
  }
}
