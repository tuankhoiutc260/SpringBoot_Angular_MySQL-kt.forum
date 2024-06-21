import { Component, OnInit } from '@angular/core';
import { Role } from '../../../core/interface/role';
import { User } from '../../../core/interface/user';
import { RoleService } from '../../../core/service/role.service';
import { UserService } from '../../../core/service/user.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss', // Sửa lỗi cú pháp
  providers: [MessageService]
})
export class UserManagementComponent implements OnInit { // Implement OnInit

  roles: Role[] = []; // Khởi tạo mảng rỗng để tránh lỗi không xác định
  users: User[] = []; // Khởi tạo mảng rỗng để tránh lỗi không xác định
  isVisible: boolean = false;
  user: User = {}; // Khởi tạo đối tượng rỗng để tránh lỗi không xác định
  isEdit: boolean = false;

  constructor(
    private roleService: RoleService,
    private userService: UserService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.getRoles();
    this.getAllUsers(); // Sửa lỗi tên phương thức
  }

  getRoles(): void {
    this.roleService.getAllRoles().subscribe({
      next: (roles: Role[]) => {
        this.roles = roles;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  getAllUsers(): void { // Sửa lỗi tên phương thức
    this.userService.getAllUsers().subscribe({
      next: (users: User[]) => {
        this.users = users;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  getRoleByID(roleID: number) {
    return this.roles.find(role => role.id === roleID)?.name;
  }

  openDialogEdit(user: User): void { // Thay đổi kiểu của user thành User
    this.isEdit = true;
    this.isVisible = true;
    this.user = { ...user };
  }

  openDialogNew(): void {
    this.isEdit = false;
    this.isVisible = true;
    this.user = {
      role_id: 1,
      active: true
    } as User; // Chuyển đối tượng sang User để tránh lỗi kiểu
  }

  saveUser(): void {
    if (this.user.id) {
      this.userService.update(this.user.id, this.user).subscribe({
        next: () => {
          this.isVisible = false;
          const index = this.users.findIndex(user => user.id === this.user.id);
          if (index !== -1) {
            this.users[index] = this.user;
          }
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User is updated' });
          // window.location.reload();
        },
        error: (error) => {
          console.log(error);
        }
      });
    } else {
      this.userService.create(this.user).subscribe({
        next: () => { // Change to accept createdUser of type User
          this.isVisible = false;
          this.users.push(this.user);
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User is created' });
          // window.location.reload();
        },
        error: (error) => {
          console.log(error);
        }
      });
    }
  }


}
