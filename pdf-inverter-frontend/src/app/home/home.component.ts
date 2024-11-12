import { Component } from '@angular/core';
import { HttpEventType } from '@angular/common/http';
import { UrlserviceService } from '../services/urlservice.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  darkMode: boolean = false;
  selectedFile: File | null = null;
  downloadLink: string | null = null;
  loading: boolean = false;
  progress: number = 0;

  constructor(private urlService: UrlserviceService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file?.name.endsWith('.pdf')) {
      alert('Please select a PDF file.');
      this.selectedFile = null;
      this.downloadLink = null;
      this.progress = 0;
      return;
    }

    if (file && file.size <= 15 * 1024 * 1024) {
      this.selectedFile = file;
      this.downloadLink = null;
      this.progress = 0;
    } else {
      alert('File is too large. Please select a file smaller than 15MB.');
      this.selectedFile = null;
    }
  }

  invertPdf() {
    if (this.selectedFile) {
      this.loading = true;
      this.progress = 0;
      this.downloadLink = null;

      this.urlService.invertFile(this.selectedFile).subscribe({
        next: (event: any) => {
          if (event.type === HttpEventType.UploadProgress && event.total) {
            this.progress = Math.round((100 * event.loaded) / event.total);
          } else if (event.type === HttpEventType.Response) {
            this.loading = false;
            const url = window.URL.createObjectURL(event.body);
            this.downloadLink = url;
          }
        },
        error: (error) => {
          this.loading = false;
          console.log(error);
          alert('Error processing the PDF.');
        },
      });
    }
  }

  toggleDarkMode(): void {
    this.darkMode = !this.darkMode;
  }
}
