import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRole = route.data?.['expectedRole'];
  const currentRole = authService.getRole();

  if (authService.isAuthenticated() && currentRole === expectedRole) {
    return true;
  } else {
    router.navigate(['/login'], {
      queryParams: {
        returnUrl: state.url,
        message: currentRole === expectedRole ? 'Login expired, please Login again!' : 'Your Account don\'t have Permission. Please Login again!'
      }
    });
    return false;
  }
};
