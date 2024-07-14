import { Component, OnInit } from '@angular/core';
import { RoleService } from '../../../core/service/role.service';
import { UserService } from '../../../core/service/user.service';
import { MessageService } from 'primeng/api';
import { ApiResponse } from '../../../core/interface/response/apiResponse';
import { RoleResponse } from '../../../core/interface/response/role-response';
import { UserResponse } from '../../../core/interface/response/user-response';
import { UserRequest } from '../../../core/interface/request/user-request';
import { RoleIdToNamePipe } from '../../../core/pipe/role-id-to-name.pipe';

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
  userRequest: UserRequest = {};
  isEdit: boolean = false;
  userRequestID: string | null = null; // ID của bài viết, nếu đang cập nhật


  constructor(
    private roleService: RoleService,
    private userService: UserService,
    private messageService: MessageService,
  ) { }

  ngOnInit() {
    this.getAllRoles();
    this.getAllUsers();
  }
  onDropdownChange(selectedValue: any) {
    console.log('Selected value:', selectedValue);
    // Điều gì đó khác ở đây với lựa chọn bộ lọc
  }

  getAllRoles() {
    this.roleService.findAll().subscribe({
      next: (apiResponse: ApiResponse<RoleResponse[]>) => {
        const roleResponseList = apiResponse.result;
        if (roleResponseList) {
          this.rolesResponse = roleResponseList;
        }
        else {
          console.error('No result found in response: ', apiResponse.message)
        }
      },
      error: (error) => {
        console.error('Error fetching posts: ', error)
      }
    });
  }

  onRoleChange(roleId: any) {
    if (this.userResponse.role) {
      this.userResponse.role.id = roleId;
    } else {
      this.userResponse.role = { id: roleId };
    }
  }

  getAllUsers() {
    this.userService.findAll().subscribe({
      next: (apiResponse: ApiResponse<UserResponse[]>) => {
        const userResponseList = apiResponse.result;
        if (userResponseList) {
          this.usersResponse = userResponseList
        }
        else {
          console.error('No result found in response: ', apiResponse.message)
        }
      },
      error: (apiResponse: ApiResponse<UserResponse>) => {
        console.error(apiResponse.message)
      }
    });
  }

  openDialogEdit(userResponse: UserResponse): void {
    this.isEdit = true;
    this.isVisible = true;
    this.userResponse = { ...userResponse };
  }

  openDialogNew(): void {
    this.isEdit = false;
    this.isVisible = true;
    this.userResponse = {
      role: {
        id: 1
      },
      active: true
    } as UserResponse;
  }

  saveUser() {
    // this.userRequest = {
    //   ...this.userResponse,
    //   role: this.userResponse.role?.id
    // };
    // this.userRequestID = this.userResponse.id ?? null;

    // this.userService.save(this.userRequestID, this.userRequest).subscribe({
    //   next: (apiResponse: ApiResponse<UserResponse>) => {
    //     const userResponse = apiResponse.result;
    //     if (userResponse) {
    //       if (this.userRequestID) {
    //         const index = this.usersResponse.findIndex(user => user.id === this.userRequestID);
    //         if (index !== -1) {
    //           this.usersResponse[index] = userResponse;
    //         }
    //         this.showMessage('info', 'Confirmed', 'User updated');
    //       } else {
    //         this.usersResponse.unshift(userResponse);
    //         this.showMessage('info', 'Confirmed', 'User created');
    //       }
    //       this.isVisible = false;
    //       this.resetForm();
    //       this.loadPage();
    //     } else {
    //       console.error('No result found in response:', apiResponse);
    //     }
    //   },
    //   error: (apiResponse: ApiResponse<UserResponse>) => {
    //     this.showMessage('error', 'Error', apiResponse.message ?? '');
    //   }
    // });
  }

  resetForm() {
    this.userRequest = {};
    this.userRequestID = null;
  }

  loadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  showMessage(severityRequest: string, summaryRequest: string, detailRequest: string) {
    this.messageService.add({ severity: severityRequest, summary: summaryRequest, detail: detailRequest });
  }
}