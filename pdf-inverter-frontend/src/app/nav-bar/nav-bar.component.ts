import { Component } from '@angular/core';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss',
})
export class NavBarComponent {
  toggleTheme() {
    document.body.classList.toggle('dark-mode');
    console.log(document.body.classList); // Check if 'dark-mode' is added or removed
  }
}
