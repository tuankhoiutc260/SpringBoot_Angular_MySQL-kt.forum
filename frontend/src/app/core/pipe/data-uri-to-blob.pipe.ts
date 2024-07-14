import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dataUriToBlob'
})
export class DataUriToBlobPipe implements PipeTransform {

  // dataURItoBlob(dataURI: string) {
  //   const byteString = window.atob(dataURI);
  //   const arrayBuffer = new ArrayBuffer(byteString.length);
  //   const int8Array = new Uint8Array(arrayBuffer);
  //   for (let i = 0; i < byteString.length; i++) {
  //     int8Array[i] = byteString.charCodeAt(i);
  //   }
  //   const blob = new Blob([int8Array], { type: 'image/png' });
  //   return blob;
  // }

  // transform(value: string): Blob {
  //   const base64 = value
  //   const imageBlob = this.dataURItoBlob(base64);
  //   const imageFile = new File([imageBlob], null!, { type: 'image/png' });
  //   return imageFile;
  // }

  transform(value: string): string {
    const image = ('data:image/png;base64,'+ value);
    return  image;
  }




}
