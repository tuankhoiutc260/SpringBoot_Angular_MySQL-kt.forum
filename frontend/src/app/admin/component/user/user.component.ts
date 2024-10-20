import { Component, OnDestroy, OnInit } from '@angular/core';
import { Table } from 'primeng/table';
import { UserResponse } from '../../../api/model/response/user-response';
import { UserApiService } from '../../../api/service/rest-api/user-api.service';
import { catchError, Observable, Subject, takeUntil, throwError } from 'rxjs';
import { PagedResponse } from '../../../api/model/response/paged-response';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss',
  providers: [MessageService]
})
export class UserComponent implements OnInit, OnDestroy {
  readonly ADMIN_ROLE = 1;
  usersCurrentPage = 0;
  readonly USERS_PAGE_SIZE = 10;
  userResponsePage$!: Observable<PagedResponse<UserResponse[]>>;

  isEditUserInfo = false;

  private destroy$ = new Subject<void>();

  constructor(
    private userApiService: UserApiService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
      this.initializeUsers(this.usersCurrentPage, this.USERS_PAGE_SIZE);
      
  }

  initializeUsers(page: number, size: number){
    this.userResponsePage$ = this.userApiService.getAll(page, size).pipe(
      takeUntil(this.destroy$),
      catchError(error => {
        console.error('Error fetching Users: ', error);
        this.messageService.add({severity: 'error', summary: 'Lỗi', detail: 'Lỗi khi tải danh sách User'});
        return throwError(() => new Error(error.message || 'Server error'));
      })      
    )
  }

//   getSeverity(status: string) {
//     console.log(status)
//     switch (status) {
//         case 'false':
//             return 'danger';

//         case 'true':
//             return 'success';

//         case 'new':
//             return 'info';

//         case 'negotiation':
//             return 'warning';

//         case 'renewal':
//             return undefined; // Hoặc trả về giá trị mặc định nào đó, ví dụ: 'secondary'
//         default:
//             return undefined; // Nếu không có trạng thái nào phù hợp
//     }
// }




  customers!: UserResponse[];

  // representatives!: Representative[];

  statuses!: any[];

  loading: boolean = false;

  activityValues: number[] = [0, 100];

  searchValue: string | undefined;


  // ngOnInit() {
  //     this.customerService.getCustomersLarge().then((customers) => {
  //         this.customers = customers;
  //         this.loading = false;

  //         this.customers.forEach((customer) => (customer.date = new Date(<Date>customer.date)));
  //     });

  //     this.representatives = [
  //         { name: 'Amy Elsner', image: 'amyelsner.png' },
  //         { name: 'Anna Fali', image: 'annafali.png' },
  //         { name: 'Asiya Javayant', image: 'asiyajavayant.png' },
  //         { name: 'Bernardo Dominic', image: 'bernardodominic.png' },
  //         { name: 'Elwin Sharvill', image: 'elwinsharvill.png' },
  //         { name: 'Ioni Bowcher', image: 'ionibowcher.png' },
  //         { name: 'Ivan Magalhaes', image: 'ivanmagalhaes.png' },
  //         { name: 'Onyama Limba', image: 'onyamalimba.png' },
  //         { name: 'Stephen Shaw', image: 'stephenshaw.png' },
  //         { name: 'Xuxue Feng', image: 'xuxuefeng.png' }
  //     ];

  //     this.statuses = [
  //         { label: 'Unqualified', value: 'unqualified' },
  //         { label: 'Qualified', value: 'qualified' },
  //         { label: 'New', value: 'new' },
  //         { label: 'Negotiation', value: 'negotiation' },
  //         { label: 'Renewal', value: 'renewal' },
  //         { label: 'Proposal', value: 'proposal' }
  //     ];
  // }

  clear(table: Table) {
      table.clear();
      this.searchValue = ''
  }

  // getSeverity(status: string) {
  //     switch (status.toLowerCase()) {
  //         case 'unqualified':
  //             return 'danger';

  //         case 'qualified':
  //             return 'success';

  //         case 'new':
  //             return 'info';

  //         case 'negotiation':
  //             return 'warning';

  //         case 'renewal':
  //             return null;
  //     }
  // }

  ngOnDestroy(): void {
      
  }
}