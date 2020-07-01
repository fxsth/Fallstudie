import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
export interface Position {
  long: number;
  lat: number;
  cluster: number;
}

@Injectable({
  providedIn: "root",
})
export class PositionService {
  constructor(private http: HttpClient) {}
  getRandomMarkersDortmund() {
    return this.http.get<Position[]>(
      "http://localhost:8000/random_long_lat_cluster"
    );
  }
}
