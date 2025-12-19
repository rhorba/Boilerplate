import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../core/services/authentication.service';
import { Router } from '@angular/router';
import { PageService } from '../../core/services/page.service';

export interface MenuItem {
  label: string;
  icon?: string;
  route: string;
  roles?: string[]; // Allowed roles. If empty/undefined, allowed for all.
}

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  userEmail: string | null = '';
  userRole: string | null = '';

  // Configurable Generic Menu
  allMenuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard', roles: ['USER', 'ADMIN'] },
    { label: 'Profile', icon: 'person', route: '/profile', roles: ['USER', 'ADMIN'] },
    { label: 'Settings', icon: 'settings', route: '/settings', roles: ['ADMIN'] },
  ];

  displayedMenuItems: MenuItem[] = [];

  pageTitle: string = 'Dashboard';

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private pageService: PageService
  ) {
    this.router.events.subscribe(() => {
      this.updateTitle();
    });
  }

  ngOnInit(): void {
    this.userEmail = this.authService.getUserEmail();
    this.userRole = this.authService.getUserRole();
    this.loadDynamicPages();

    // Subscribe to page changes
    this.pageService.refreshPages.subscribe(() => {
      this.loadDynamicPages();
    });

    this.updateTitle();
  }

  loadDynamicPages() {
    this.pageService.getAllPages().subscribe((pages: any[]) => {
      const pageItems: MenuItem[] = pages.map((page: any) => ({
        label: page.title,
        icon: page.icon || 'article',
        route: '/pages/' + page.slug,
        roles: page.roles ? page.roles.split(',') : []
      }));

      // Reset and rebuild menu
      this.allMenuItems = [
        { label: 'Dashboard', icon: 'dashboard', route: '/dashboard', roles: ['USER', 'ADMIN'] },
        { label: 'Profile', icon: 'person', route: '/profile', roles: ['USER', 'ADMIN'] },
        { label: 'Settings', icon: 'settings', route: '/settings', roles: ['ADMIN'] },
        ...pageItems
      ];

      this.filterMenu();
    }, (error) => {
      console.error('Failed to load pages', error);
      // Fallback to static menu
      this.filterMenu();
    });
  }

  updateTitle() {
    const currentRoute = this.router.url;
    const activeItem = this.allMenuItems.find(item => currentRoute.includes(item.route));
    if (activeItem) {
      this.pageTitle = activeItem.label;
    } else {
      this.pageTitle = 'Dashboard';
    }
  }

  filterMenu() {
    console.log('Current User Role:', this.userRole);
    if (!this.userRole) {
      this.displayedMenuItems = [];
      return;
    }
    this.displayedMenuItems = this.allMenuItems.filter(item => {
      if (!item.roles || item.roles.length === 0) return true;
      const hasRole = item.roles.some(r => r.toUpperCase() === this.userRole?.toUpperCase());
      console.log(`Item: ${item.label}, Roles: ${item.roles}, UserRole: ${this.userRole}, Visible: ${hasRole}`);
      return hasRole;
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
