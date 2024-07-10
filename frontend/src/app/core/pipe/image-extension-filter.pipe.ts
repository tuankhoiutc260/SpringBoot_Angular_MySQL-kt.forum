import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'imageExtensionFilter'
})
export class ImageExtensionFilterPipe implements PipeTransform {
  private acceptedExtensions: string[] = ['jpg', 'jpeg', 'png', 'gif'];

  transform(value: string): string {
    const extension = value.split('.').pop()?.toLowerCase();
    if (extension && this.acceptedExtensions.includes(extension)) {
      return value;
    } else {
      // Return default image or handle invalid extension
      return 'default-image.png';
    }
  }
}
