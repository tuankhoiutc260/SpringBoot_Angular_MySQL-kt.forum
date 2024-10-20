import { Component, OnInit, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-body-section',
  templateUrl: './body-section.component.html',
  styleUrls: ['./body-section.component.scss'],
  providers: [MessageService],
})
export class BodySectionComponent implements OnChanges {
  @Input() scrollToSection: boolean = false;
  @Output() scrollToSectionComplete = new EventEmitter<void>();
  
  ngOnChanges() {
    if (this.scrollToSection) {
      this.scrollTo();
    }
  }
  
  scrollTo() {
    const element = document.getElementById('sc-body');
    if (element) {
      const elementPosition = element.getBoundingClientRect().top + window.scrollY;
      
      window.scrollTo({
        top: elementPosition - 73,  
        behavior: 'smooth'  
      });
  
      this.scrollToSectionComplete.emit();
    }
  }
}
