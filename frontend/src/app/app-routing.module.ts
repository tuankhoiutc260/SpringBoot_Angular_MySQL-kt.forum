import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user/user.component';

const routes: Routes = [
  // {
  //   path: 'login',
  //   component: LoginComponent,
  //   title: 'Login'
  // },
  // {
  //   path: '',
  //   component: UserComponent,
  //   title: 'KT Forum',
  //   loadChildren: () =>
  //     import('./user/user.module').then(m => m.UserModule)
  // },
  {
    path: 'login',
    loadChildren: () => import('./features/login/login.module').then(m => m.LoginModule)
  },


  // {
  //   path: '',
  //   loadChildren: () => import('./user/user.module').then(m => m.UserModule)
  // },

  {
    path: '',
    component: UserComponent,
    loadChildren: () =>
      import('./user/user.module').then(m => m.UserModule)
  },
  // {
  //   path: '404',
  //   component: NotFoundComponent,
  //   title: '404'
  // },
  // {
  //   path: '**',
  //   redirectTo: '404'
  // }
];


// const routes: Routes = [
//   {
//     path: '',
//     component: UserComponent,
//     title: 'KT Forum',
//     loadChildren: () =>
//       import('./user/user.module').then(m => m.UserModule)
//   },
//   {
//     path: 'login',
//     component: LoginComponent,
//     title: 'Login'
//   },
//   {
//     path: '404',
//     component: NotFoundComponent,
//     title: '404'
//   }
//   // {
//   //   path: '**',
//   //   redirectTo: '/404'
//   // }

//   // {
//   //   component: StaffComponent,
//   //   path: 'management',
//   //   canActivate: [authGuard],
//   //   data: { expectedRole: 'ROLE_ADMIN' || 'ROLE_STAFF' },
//   //   // data: { expectedRole: ['ROLE_ADMIN', 'ROLE_STAFF'] },

//   //   loadChildren: () =>
//   //     import('./staff/staff.module').then(m => m.StaffModule)
//   // },
// ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
