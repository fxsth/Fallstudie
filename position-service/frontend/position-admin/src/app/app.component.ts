import { Component, OnInit } from "@angular/core";
import { tileLayer, latLng, marker, Icon } from "leaflet";
import { PositionService } from "./services/position.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit {
  showMap = false;
  title = "position-admin";
  options = {
    layers: [
      tileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 22,
        attribution: "...",
      }),
    ],
    zoom: 13,
    center: latLng(51.514244, 7.468429),
  };
  layers = [marker([51.514244, 7.469429])];
  constructor(private positionService: PositionService) {}

  ngOnInit(): void {
    this.positionService.getRandomMarkersDortmund().subscribe((positions) => {
      positions.forEach((position) => {
        let url;
        switch (position.cluster) {
          case 0:
            url = "../assets/m4.png";
            break;
          case 1:
            url = "../assets/m1.png";
            break;
          case 2:
            url = "../assets/m2.png";
            break;
          case 3:
            url = "../assets/m3.png";
            break;
          default:
            break;
        }
        this.layers.push(
          marker([position.lat, position.long], {
            icon: new Icon({
              iconUrl: url,
            }),
          })
        );
      });
      setTimeout((_) => {
        this.showMap = true;
      }, 200);
    });
  }
}
