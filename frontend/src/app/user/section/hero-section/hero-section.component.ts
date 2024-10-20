import { Component, ElementRef, EventEmitter, Output, Renderer2, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-hero-section',
  templateUrl: './hero-section.component.html',
  styleUrls: ['./hero-section.component.scss']
})
export class HeroSectionComponent implements OnInit, OnDestroy {
  @Output() onLetStarted = new EventEmitter<void>();
  countPosts = 0;
  countPostsStop: any;

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {
    this.countPostsStop = setInterval(() => {
      this.countPosts++;
      if(this.countPosts === 123){
        clearInterval(this.countPostsStop);
      }
    }, 10);
  }

  ngOnDestroy(): void {
    if (this.countPostsStop) {
      clearInterval(this.countPostsStop);
    }
  }

  onLetStartedClicked(){
    this.onLetStarted.emit();
  }
}
