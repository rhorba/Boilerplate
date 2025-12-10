import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Page, PageService } from '../../core/services/page.service';
import { AuthenticationService } from '../../core/services/authentication.service';
import { PageData, PageDataService } from '../../core/services/page-data.service';

@Component({
  selector: 'app-dynamic-page',
  templateUrl: './dynamic-page.component.html',
  styleUrls: ['./dynamic-page.component.scss']
})
export class DynamicPageComponent implements OnInit {

  page: Page | null = null;
  loading = true;
  error = '';

  // Dynamic Data
  schema: any[] = [];
  dataList: PageData[] = [];
  currentData: any = {}; // The actual data object being edited
  currentDataId: number | null = null;
  isEditing = false;

  // Access Control
  permissions: { [key: string]: string[] } = {};
  userRole: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private pageService: PageService,
    private pageDataService: PageDataService,
    private authService: AuthenticationService
  ) { }

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();
    this.route.paramMap.subscribe(params => {
      const slug = params.get('slug');
      if (slug) {
        this.loadPage(slug);
      }
    });
  }

  loadPage(slug: string) {
    this.loading = true;
    this.pageService.getPageBySlug(slug).subscribe({
      next: (data: Page) => {
        this.page = data;
        this.schema = data.schema ? JSON.parse(data.schema) : [];
        this.permissions = data.accessControl ? JSON.parse(data.accessControl) : {};
        this.loadData();
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Page not found';
        this.loading = false;
      }
    });
  }

  loadData() {
    if (this.page && this.page.id) {
      this.pageDataService.getAllDataByPageId(this.page.id).subscribe({
        next: (data: PageData[]) => {
          this.dataList = data;
        },
        error: (err: any) => console.error('Failed to load data', err)
      });
    }
  }

  getParsedData(json: string): any {
    try {
      return JSON.parse(json);
    } catch (e) {
      return {};
    }
  }

  getOptions(optionsString?: string): string[] {
    if (!optionsString) return [];
    return optionsString.split(',').map(opt => opt.trim());
  }

  addData() {
    this.currentData = {};
    this.currentDataId = null;
    this.isEditing = true;
  }

  editData(item: PageData) {
    this.currentData = JSON.parse(item.data);
    this.currentDataId = item.id!;
    this.isEditing = true;
  }

  deleteData(id: number) {
    if (confirm('Are you sure?')) {
      this.pageDataService.deletePageData(id).subscribe(() => {
        this.loadData();
      });
    }
  }

  saveData() {
    if (!this.page?.id) return;

    const pageData: PageData = {
      id: this.currentDataId || undefined,
      pageId: this.page.id,
      data: JSON.stringify(this.currentData)
    };

    if (this.currentDataId) {
      this.pageDataService.updatePageData(this.currentDataId, pageData).subscribe(() => {
        this.loadData();
        this.isEditing = false;
      });
    } else {
      this.pageDataService.createPageData(pageData).subscribe(() => {
        this.loadData();
        this.isEditing = false;
      });
    }
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentData = {};
    this.currentDataId = null;
  }

  hasPermission(action: string): boolean {
    if (!this.userRole) return false;
    // If no permissions defined, default to allowing ADMIN, blocking others? 
    // Or default to allow all? Let's default to allow if not specified, or strict?
    // Based on settings init, we default to some values.
    // If the permission object exists but the action key is missing, maybe allow?
    // Let's be strict: if permissions exist, check them. If not, fallback to ADMIN only or allow all?
    // Let's assume if empty, allow all (backward compatibility) OR check if 'create' exists.

    if (Object.keys(this.permissions).length === 0) {
      // No permissions configured, allow everything (or maybe just ADMIN?)
      // For now, let's allow everything to not break existing pages
      return true;
    }

    const allowedRoles = this.permissions[action] || [];
    return allowedRoles.includes(this.userRole);
  }
}
