import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UrlserviceService } from '../services/urlservice.service';
import { HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  selectedFile: File | null = null;
  downloadLink: string | null = null;
  loading = false;
  progress = 0;

  constructor(private urlService: UrlserviceService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file && file.size <= 15 * 1024 * 1024) {
      this.selectedFile = file;
    } else {
      alert('File is too large. Please select a file smaller than 15MB.');
    }
  }

  invertPdf() {
    if (this.selectedFile) {
      this.loading = true;
      this.progress = 0;

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
        error: () => {
          this.loading = false;
          alert('Error processing the PDF.');
        },
      });
    }
  }
}
