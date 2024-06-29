import { Component, OnInit } from '@angular/core';
import { RoleService } from '../../../core/service/role.service';
import { UserService } from '../../../core/service/user.service';
import { MessageService } from 'primeng/api';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { RoleResponse } from '../../../core/interface/response/role-response';
import { UserResponse } from '../../../core/interface/response/user-response';
import { RoleRequest } from '../../../core/interface/request/role-request';
import { UserRequest } from '../../../core/interface/request/user-request';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss', // Sửa lỗi cú pháp
  providers: [MessageService]
})
export class UserManagementComponent implements OnInit { // Implement OnInit
  rolesResponse: RoleResponse[] = [];
  roleResponse: RoleResponse = {};
  usersResponse: UserResponse[] = [];
  isVisible: boolean = false;
  userResponse: UserResponse = {};
  isEdit: boolean = false;

  constructor(
    private roleService: RoleService,
    private userService: UserService,
    private messageService: MessageService
  ) { }

  ngOnInit() {
    this.getAllRoles();
    this.getAllUsers();
  }

  getAllRoles() {
    this.roleService.findAll<RoleResponse>().subscribe(
      (apiResponse: ApiResponse<RoleResponse>) => {
        if (Array.isArray(apiResponse.result)) {
          this.rolesResponse = apiResponse.result;
        } else {
          this.rolesResponse = [];
        }
      },
      (error) => {
        console.log(error);
      }
    );
  }

  getAllUsers() {
    this.userService.findAll<UserResponse>().subscribe(
      (apiResponse: ApiResponse<UserResponse>) => {
        if (Array.isArray(apiResponse.result)) {
          this.usersResponse = apiResponse.result;
        } else {
          this.usersResponse = [];
        }
      },
      (error) => {
        console.log(error);
      }
    );
  }

  // getRoleByID(roleID: number) {
  //   // return this.role.find(role => role.id === roleID)?.name;
  //   return this.roleService.findByID<RoleRequest>(roleID);
  // }

  openDialogEdit(userResponse: UserResponse): void {
    this.isEdit = true;
    this.isVisible = true;
    this.userResponse = { ...userResponse };
    console.log(this.userResponse)
  }



  openDialogNew(): void {
    this.isEdit = false;
    this.isVisible = true;
    this.userResponse = {
      role_id: 1,
      active: true
    } as UserResponse; // Chuyển đối tượng sang User để tránh lỗi kiểu
  }

  saveUser(): void {
    // if (this.user.id) {
    //   // Updating existing user
    //   const updatedUser: Partial<User> = { ...this.user };
    //   delete updatedUser.createdDate;
    //   delete updatedUser.createdBy;
    //   delete updatedUser.lastModifiedDate;
    //   delete updatedUser.lastModifiedBy;

    //   this.userService.update<User>(this.user.id, updatedUser).subscribe({
    //     next: () => {
    //       this.isVisible = false;
    //       const index = this.users.findIndex(user => user.id === this.user.id);
    //       if (index !== -1) {
    //         this.users[index] = { ...this.users[index], ...this.users };
    //       }
    //       this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User is updated' });
    //     },
    //     error: (error) => {
    //       console.log('Error updating user:', error);
    //     }
    //   });
    // } else {
    //   // Creating new user
    //   const newUser: Partial<User> = { ...this.user };
    //   delete newUser.id;
    //   delete newUser.createdDate;
    //   delete newUser.createdBy;
    //   delete newUser.lastModifiedDate;
    //   delete newUser.lastModifiedBy;

    //   this.userService.create<User>(newUser as User).subscribe({
    //     next: (apiResponse: ApiResponse<User>) => {
    //       if (apiResponse.result) {
    //         const id = apiResponse.result.id;
    //         this.isVisible = false;
    //         const newUserWithId = { ...newUser, id };
    //         this.users.push(newUserWithId);
    //         this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User is created' });
    //       } else {
    //         console.log('Error creating user: Response does not contain user ID');
    //       }
    //     },
    //     error: (error) => {
    //       console.log('Error creating user:', error);
    //     }
    //   });
    // }
  }

  set userRoleId(roleId: number | undefined) {
    // if (this.user.role) {
    //   this.user.role.id = roleId;
    // } else {
    //   this.user.role = { id: roleId } as Role;
    // }
  }

  get userRoleId(): number | undefined {
    return this.userResponse.role?.id;
  }

}