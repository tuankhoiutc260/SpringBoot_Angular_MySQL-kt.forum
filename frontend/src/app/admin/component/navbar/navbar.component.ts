import { Component } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  sidebarVisible: boolean = true;
  openMenus: { [key: string]: boolean } = {};

  toggleMenu(menuKey: string): void {
    this.openMenus[menuKey] = !this.openMenus[menuKey];
  }

  isMenuOpen(menuKey: string): boolean {
    return this.openMenus[menuKey] || false;
  }
}