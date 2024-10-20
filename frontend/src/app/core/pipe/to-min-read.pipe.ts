import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toMinRead'
})
export class ToMinReadPipe implements PipeTransform {
  transform(text: string): number {
    const wordsPerMinute = 200;
    const cleanText = text.replace(/[^\w\s]/gi, '');
    const textLength = cleanText.split(/\s+/).length;
    return Math.ceil(textLength / wordsPerMinute);
  }
}
