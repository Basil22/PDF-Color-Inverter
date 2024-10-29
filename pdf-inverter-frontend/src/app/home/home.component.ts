import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  providers: [HttpClient],
})
export class HomeComponent {
  selectedFile: File | null = null;
  downloadLink: string | null = null;

  constructor(private http: HttpClient) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file && file.size <= 10 * 1024 * 1024) {
      this.selectedFile = file;
    } else {
      alert('File is too large. Please select a file smaller than 10MB.');
    }
  }

  invertPdf() {
    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      this.http
        .post('http://localhost:8080/invert', formData, {
          responseType: 'blob',
        })
        .subscribe((response: Blob) => {
          const url = window.URL.createObjectURL(response);
          this.downloadLink = url;
        });
    }
  }
}
