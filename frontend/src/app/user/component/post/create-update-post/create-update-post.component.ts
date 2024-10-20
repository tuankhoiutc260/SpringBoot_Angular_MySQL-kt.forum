import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Observable, Subject, catchError, finalize, takeUntil, tap, throwError } from 'rxjs';
import { PostApiService } from '../../../../api/service/rest-api/post-api.service';
import { CategoryApiService } from '../../../../api/service/rest-api/category-api.service';
import { SubCategoryApiService } from '../../../../api/service/rest-api/sub-category-api.service';
import { PostRequest } from '../../../../api/model/request/post-request';
import { PostResponse } from '../../../../api/model/response/post-response';
import { CategoryResponse } from '../../../../api/model/response/category-response';
import { SubCategoryResponse } from '../../../../api/model/response/sub-category-response';
import { PagedResponse } from '../../../../api/model/response/paged-response';

@Component({
  selector: 'app-create-update-post',
  templateUrl: './create-update-post.component.html',
  styleUrls: ['./create-update-post.component.scss'],
  providers: [MessageService]
})
export class CreateUpdatePostComponent implements OnInit, OnDestroy {
  @Input() isCreatePost!: boolean;
  @Input() isDialogVisible!: boolean;
  @Input() postResponse: PostResponse | undefined;
  @Output() isDialogVisibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  postDetailsForm: FormGroup;
  categoryResponsePage$!: Observable<PagedResponse<CategoryResponse[]>>;
  subCategoryResponsePage$!: Observable<PagedResponse<SubCategoryResponse[]>>;
  selectedCategoryId: string = '';

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private postApiService: PostApiService,
    private messageService: MessageService,
    private categoryApiService: CategoryApiService,
    private subCategoryApiService: SubCategoryApiService
  ) {
    this.postDetailsForm = this.createForm();
  }

  ngOnInit() {
    this.initializeCategories(0, 100);
    if (this.postResponse) {
      this.populateForm();
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(250)]],
      description: ['', [Validators.required, Validators.maxLength(300)]],
      content: ['', [Validators.required, Validators.minLength(500)]],
      subCategoryId: ['', [Validators.required]],
      tags: [[], [Validators.required]]
    });
  }

  populateForm() {
    if (this.postResponse) {
      this.categoryApiService.getBySubCategoryId(this.postResponse.subCategoryId).pipe(
        tap(categoryResponse => {
          this.selectedCategoryId = categoryResponse.id;
          this.initializeSubCategories(this.selectedCategoryId, 0, 100);
        }),
        catchError(error => {
          this.handleError('Error loading Category', error);
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        takeUntil(this.destroy$)
      ).subscribe();

      this.postDetailsForm.patchValue({
        title: this.postResponse.title,
        description: this.postResponse.description,
        content: this.postResponse.content,
        subCategoryId: this.postResponse.subCategoryId,
        tags: this.postResponse.tags
      });
    }
  }

  clearForm(): void {
    this.postDetailsForm.reset();
    this.selectedCategoryId = '';
    this.subCategoryResponsePage$ = new Observable<PagedResponse<SubCategoryResponse[]>>();
    this.messageService.add({ severity: 'info', summary: 'Form has been reset', detail: 'The field data has been cleared' });
  }

  initializeCategories(page: number, size: number): void {
    this.categoryResponsePage$ = this.categoryApiService.getAll(page, size).pipe(
      catchError(error => {
        this.handleError('Error loading Topic', error);
        return throwError(() => new Error(error.message || 'Server error'));
      })
    );
  }

  onCategoryChange(event: any): void {
    if (event.value) {
      this.selectedCategoryId = event.value;
      this.initializeSubCategories(this.selectedCategoryId, 0, 100);
      this.postDetailsForm.get('subCategoryId')?.setValue(null);
    }
  }

  initializeSubCategories(categoryId: string, page: number, size: number): void {
    this.subCategoryResponsePage$ = this.subCategoryApiService.getByCategoryId(categoryId, page, size).pipe(
      tap(response => {
        if (response.content.length === 0) {
          this.messageService.add({ severity: 'info', summary: 'No Sub-categories', detail: 'No sub-categories found for the selected category.' });
        }
      }),
      catchError(error => {
        this.handleError('Error loading Sub-Categories', error);
        return throwError(() => new Error(error.message || 'Server error'));
      })
    );
  }

  onSubCategoryChange(event: any): void {
    if (event.value) {
      this.postDetailsForm.patchValue({ subCategoryId: event.value });
    }
  }

  onSubmit(): void {
    if (this.postDetailsForm.valid) {
      const postRequest: PostRequest = this.postDetailsForm.value;

      const apiCall = this.isCreatePost
        ? this.postApiService.create(postRequest)
        : this.postApiService.update(this.postResponse!.id, postRequest);

      apiCall.pipe(
        tap(() => {
          const message = this.isCreatePost ? 'Post created' : 'Post updated';
          this.messageService.add({ severity: 'success', summary: 'Success', detail: message });
          this.isDialogVisible = false;
          this.isDialogVisibleChange.emit(false);
        }),
        takeUntil(this.destroy$),
        catchError(error => {
          this.handleError('Error processing Post', error);
          return throwError(() => new Error(error.message || 'Server error'));
        }),
        finalize(() => {
          this.onLoadPage();
        })
      ).subscribe();
    } else {
      this.postDetailsForm.markAllAsTouched();
      this.messageService.add({ severity: 'error', summary: 'Lỗi', detail: 'Please fill in all required information' });
    }
  }

  onLoadPage() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  onDialogHide(): void {
    this.isDialogVisible = false;
    this.populateForm();
    this.isDialogVisibleChange.emit(false);
  }

  private handleError(message: string, error: any) {
    console.error(message, error);
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
