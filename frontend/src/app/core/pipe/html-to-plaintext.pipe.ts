import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'htmlToPlaintext'
})
export class HtmlToPlaintextPipe implements PipeTransform {

  transform(value: any): string {
    const temp = document.createElement('div');
    temp.innerHTML = value;
    return temp.textContent || temp.innerText || '';
  }
}
