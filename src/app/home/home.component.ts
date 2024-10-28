import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule], // You can add FormsModule or other modules if needed
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'], // Correct spelling here
})
export class HomeComponent {
  selectedFile: File | null = null;
  downloadLink: string | null = null;

  constructor(private http: HttpClient) {}

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file && file.size <= 10 * 1024 * 1024) {
      // Check file size (10MB max)
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
