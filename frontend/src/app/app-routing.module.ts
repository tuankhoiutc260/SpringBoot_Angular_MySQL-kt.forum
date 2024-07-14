import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user/user.component';
import { LoginComponent } from './shared/component/login/login.component';
import { StaffComponent } from './staff/staff.component';
import { authGuard } from './core/guard/auth.guard';
import { NotFoundComponent } from './shared/component/not-found/not-found.component';

const routes: Routes = [
  {
    path: '',
    component: UserComponent,
    loadChildren: () =>
      import('./user/user.module').then(m => m.UserModule)
  },
  {
    component: StaffComponent,
    path: 'staff',
    canActivate: [authGuard],
    data: { expectedRole: 'ROLE_ADMIN' || 'ROLE_STAFF' },
    loadChildren: () =>
      import('./staff/staff.module').then(m => m.StaffModule)
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'Login'
  },
  {
    path: '404',
    component: NotFoundComponent,
    title: '404'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
