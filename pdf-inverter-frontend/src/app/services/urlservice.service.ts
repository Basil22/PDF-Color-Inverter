import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { blob } from 'stream/consumers';

@Injectable({
  providedIn: 'root',
})
export class UrlserviceService {
  constructor(private http: HttpClient) {}

  private apiUrl = 'http://localhost:8080/invert';

  invertFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    // Create a new HTTP request with reportProgress set to true
    const req = new HttpRequest('POST', this.apiUrl, formData, {
      reportProgress: true,
      responseType: 'blob',
    });

    return this.http.request(req);
  }
}
