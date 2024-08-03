// import { Pipe, PipeTransform } from '@angular/core';

// @Pipe({
//   name: 'dataUriToBlob'
// })
// export class DataUriToBlobPipe implements PipeTransform {
//   transform(value: string): string {
//     const image = ('data:image/png;base64,'+ value);
//     return  image;
//   }
// }


// import { Pipe, PipeTransform } from '@angular/core';

// @Pipe({
//   name: 'dataUriToBlob'
// })
// export class DataUriToBlobPipe implements PipeTransform {
//   transform(value: string): string {
//     if (value.startsWith('data:')) {
//       // Đã là data URI, không cần chuyển đổi
//       return value;
//     } else if (value.startsWith('assets/')) {
//       // Đường dẫn file thông thường, trả về nguyên dạng
//       return value;
//     } else {
//       // Giả sử là base64 string, chuyển đổi thành data URI
//       return 'data:image/png;base64,' + value;
//     }
//   }
// }

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
      // Đã là data URI hoặc đường dẫn assets, trả về nguyên dạng
      return value;
    } else {
      // Giả sử là base64 string, chuyển đổi thành data URI
      return 'data:image/png;base64,' + value;
    }
  }
}