import { FormControl } from '@angular/forms';

export function passwordMatchValidator(control: FormControl): { [s: string]: boolean } | null {
  const password = control.root.get('password'); 
  const rePassword = control.value;

  if (password && rePassword !== password.value) {
    return { 'passwordMismatch': true };
  }
  return {};
}
