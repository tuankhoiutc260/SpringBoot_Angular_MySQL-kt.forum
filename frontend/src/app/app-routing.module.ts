import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user/user.component';
import { StaffComponent } from './staff/staff.component';

const routes: Routes = [
  {
    path: '',
    component: UserComponent,
    loadChildren: ()=>
      import('./user/user.module').then(m => m.UserModule)
  },
  {
    component: StaffComponent,
    path: 'staff',
    loadChildren: ()=>
      import('./staff/staff.module').then(m => m.StaffModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
