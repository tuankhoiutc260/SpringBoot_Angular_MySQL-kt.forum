import { AbstractControl, ValidationErrors } from '@angular/forms';

export function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const passwordControl = control.root.get('password') || control.root.get('newPassword'); 
  const rePassword = control.value;

  if (passwordControl && rePassword !== passwordControl.value) {
    return { passwordMismatch: true };
  }
  return null;
}
