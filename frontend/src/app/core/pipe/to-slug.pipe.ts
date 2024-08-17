import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toSlug'
})
export class ToSlugPipe implements PipeTransform {

  transform(value: string): string {
    return value
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '')
    .replace(/[\s_-]+/g, '-')
    .replace(/^-+|-+$/g, '');
  }
}