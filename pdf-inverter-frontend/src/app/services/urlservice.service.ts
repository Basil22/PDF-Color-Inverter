import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UrlserviceService {
  constructor(private http: HttpClient) {}

  private rootUrl = 'http://localhost:8080/invert';

  invertFile(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(this.rootUrl, formData);
  }
}
