import { Component } from '@angular/core';
import { Role } from '../../../core/interface/role';
import { User } from '../../../core/interface/user';
import { RoleService } from '../../../core/service/role.service';
import { UserService } from '../../../core/service/user.service';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss'
})
export class UserManagementComponent {
  roles!: Role[];
  users!: User[];
  isVisible: boolean = false

  constructor(
    private roleService: RoleService,
    private userService: UserService,
  ) {

    }

  ngOnInit() {
    this.getRoles()
    this.getUsers()
  }

  getRoles(){
    this.roleService.getRoles().subscribe({
      next: (roles) =>{
        this.roles = roles;
      },
      error:(error)=>{
        console.log(error)
      }
    })
  }

  getUsers(){
    this.userService.getUsers().subscribe({
      next: (users) =>{
        this.users = users;
      },
      error:(error)=>{
        console.log(error)
      }
    })
  }

  getRole(roleID: number){
    return this.roles.find(role=>role.id ===roleID)?.name
  }

  openDialog(user: any){
    this.isVisible = true
  }
}
