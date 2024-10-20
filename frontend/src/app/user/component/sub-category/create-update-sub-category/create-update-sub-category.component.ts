import { Component, Input, OnInit } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import { SubCategoryRequest } from '../../../../api/model/request/sub-category-request';
import { catchError, finalize, Observable, Subject, takeUntil, tap, throwError } from 'rxjs';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { PagedResponse } from '../../../../api/model/response/paged-response';
import { MessageService } from 'primeng/api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';

@Component({
  selector: 'app-create-update-sub-category',
  templateUrl: './create-update-sub-category.component.html',
  styleUrl: './create-update-sub-category.component.scss',
  providers: [MessageService]
})
export class CreateUpdateSubCategoryComponent implements OnInit {
  @Input() isCreateSubCategory!: boolean;
  @Input() subCategoryResponse: SubCategoryResponse | undefined;

  categoryResponsePage$!: Observable<PagedResponse<CategoryResponse[]>>;
  subCategoryDetailsForm: FormGroup;

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private messageService: MessageService,
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService,
  ) {
    this.subCategoryDetailsForm = this.createSubCategoryForm();

  }

  ngOnInit(): void {
    this.initializeCategories(0, 100);
  }

  createSubCategoryForm(): FormGroup {
    return this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(250)]],
      description: ['', [Validators.required, Validators.maxLength(300)]],
      coverImageFile: ['', [Validators.required]],
      categoryId: ['', [Validators.required]],
    });
  }

  initializeCategories(page: number, size: number): void {
    this.categoryResponsePage$ = this.categoryApiService.getAll(page, size).pipe(
      catchError(error => {
        console.error('Error loading categories:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Lỗi khi tải danh sách Lĩnh vực' });
        return throwError(() => new Error(error.message || 'Server error'));
      })
    );
  }

  isImageValid: boolean = true
  isActiveImage: boolean = false;
  imagePreview: SafeResourceUrl | null = null;
  fileName: string = '';


  onSelectFile(event: any): void {
    const selectedFiles = event.target.files;

    if (selectedFiles.length > 0) {
      const file: File = event.target.files[0];
      const mimeType = file.type;

      if (mimeType.match(/image\/*/) == null) {
        this.isImageValid = false;
        this.isActiveImage = false;
        this.imagePreview = ''
        this.fileName = ''
        return;
      }
      else {
        this.isImageValid = false;
        this.isImageValid = true;
      }
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = reader.result;
        this.isActiveImage = true;
        this.fileName = file.name
      }
      this.subCategoryDetailsForm.get('coverImageFile')?.setValue(file);
      reader.readAsDataURL(file);
    }
  }

  onSubmit() {
    console.log(this.subCategoryDetailsForm.value)
    const subCategoryRequest: SubCategoryRequest = this.subCategoryDetailsForm.value;

    // const apiCall = this.isCreateSubCategory
    //   ? this.subCategoryApiService.create(subCategoryRequest)
    //   : this.subCategoryApiService.update(this.subCategoryResponse!.id, subCategoryRequest);

      const apiCall =  this.subCategoryApiService.create(subCategoryRequest)

    apiCall.pipe(
      tap(() => {
        const message = this.isCreateSubCategory ? 'Đã tạo chủ đề mới' : 'Chủ đề đã được chỉnh sửa';
        this.messageService.add({ severity: 'success', summary: 'Success', detail: message });
        // this.isDialogVisible = false;
        // this.isDialogVisibleChange.emit(false);
      }),
      takeUntil(this.destroy$),
      catchError(error => {
        console.error('Error during post operation:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error processing post' });
        return throwError(() => new Error(error.message || 'Server error'));
      }),
      finalize(() => {
        this.onLoadPage();
      })
    ).subscribe();
  }

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

}