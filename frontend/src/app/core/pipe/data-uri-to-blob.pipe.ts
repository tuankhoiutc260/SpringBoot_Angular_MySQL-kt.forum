import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dataUriToBlob'
})
export class DataUriToBlobPipe implements PipeTransform {
  transform(value: string | undefined): string {
    if (!value) {
      return '';
    }
    if (value.startsWith('data:') || value.startsWith('assets/')) {
      return value;
    } else {
      return 'data:image/png;base64,' + value;
    }
  }
}