import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dataUriToBlob'
})
export class DataUriToBlobPipe implements PipeTransform {
  transform(value: string): string {
    const image = ('data:image/png;base64,'+ value);
    return  image;
  }
}
