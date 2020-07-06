
"""
Calculate the point in time of packstation
1. Calculate the Positions in time with biasis toward being near in the timeframe



"""


from fastapi import FastAPI, Depends
from starlette.middleware.cors import CORSMiddleware
from db.session import Pakete, Ort, SessionLocal, Zustellbasis
from sqlalchemy.orm import Session
from schemas.paket import PaketRep
from sklearn.cluster import KMeans
from sklearn.cluster import KMeans
from schemas.utils import ceil_dt
from datetime import datetime, timedelta, timezone
from geo.geo import check_if_long_lat_array_inside_circe
from itertools import compress

import random
import numpy as np

DEBUG = True


app = FastAPI()
origins = [
    "http:localhost",
    "http:localhost:4200",
    "http://localhost:4200",
    "https://localhost:4200",
]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


def get_db():
    try:
        db = SessionLocal()
        yield db
    finally:
        db.close()


@app.get("/")
def info_root():
    return {"Version": "1.0.0"}


@app.get("/random_long_lat")
def get_random_long_lat():
    long_lat_list = []
    for i in range(20):
        long_minutes = random.randint(-150, 330)
        lat_minutes = random.randint(-18, 18)
        lat = (5_151_000 + long_minutes) / 100000
        long = (7468 + lat_minutes) / 1_000
        lat_long_dict = {"long": long, "lat": lat}
        long_lat_list.append(lat_long_dict)
    return long_lat_list


@app.get("/random_long_lat_cluster")
def create_clustering():
    long_lat_list = []
    for i in range(30):
        long_minutes = random.randint(-150, 330)
        lat_minutes = random.randint(-18, 18)
        lat = (5_151_000 + long_minutes) / 100000
        long = (7468 + lat_minutes) / 1_000
        lat_long_tupel = (long, lat)
        long_lat_list.append(lat_long_tupel)
    data_array = np.array(long_lat_list)
    kmeans_result = KMeans(
        n_clusters=4, random_state=0).fit_predict(data_array)
    result_dict_list = []
    for long_lat, cluster in zip(long_lat_list, kmeans_result):
        lat_long_result_dict = {
            "long": long_lat[0], "lat": long_lat[1], "cluster": int(cluster)}
        result_dict_list.append(lat_long_result_dict)
    return result_dict_list


def get_pakete(db: Session = Depends(get_db)):
    db = SessionLocal()
    answer = db.query(Pakete, Ort).filter(
        Pakete.wunschort_id != None).join(Ort, Pakete.wunschort_id == Ort.id).all()
    pakete_list = []

    for row in answer:
        paket = row[0]
        ort = row[1]
        paket_rep = PaketRep(id=paket.id, groesse=paket.groesse,
                             fach_nummer=paket.fach_nummer, empfaenger_id=paket.empfaenger_id,  absender_id=paket.absender_id, wunschort_id=paket.wunschort_id, long=ort.long, lat=ort.lat, interaktion_von=paket.interaktion_von, interaktion_bis=paket.interaktion_bis)
        pakete_list.append(paket_rep)
        if (DEBUG):
            paket.interaktion_von = datetime.now(
                timezone.utc)
            paket.interaktion_bis = datetime.now(
                timezone.utc) + timedelta(minutes=45)
            db.add(paket)
    db.commit()
    return pakete_list


def redistribute_old_and_new_pakete_to_zustellbasen():
    pakete_list = get_pakete()
    start = ceil_dt(datetime.now(timezone.utc), timedelta(minutes=30)
                    )
    end = ceil_dt(datetime.now(timezone.utc), timedelta(minutes=30)
                  ) + timedelta(minutes=(30))
    print(f"Alle Pakete ohne Packstation: {len(pakete_list)}")
    pakete_for_next_timeslot = []
    print(start)
    print(end)
    for paket in pakete_list:
        print(paket.interaktion_bis)
        if (paket.zustellbasis_id is not None) or ((paket.interaktion_von is not None and paket.interaktion_bis is not None) and ((start <= paket.interaktion_von <= end) or (start <= paket.interaktion_bis <= end) or (paket.interaktion_von < start and end < paket.interaktion_bis))):
            pakete_for_next_timeslot.append(paket)
    print(f"Alle Pakete für den Timeslot: {len(pakete_for_next_timeslot)}")
    coordinates = [(paket.lat, paket.long) for paket in pakete_list]
    center_point = (51.514244, 7.468429)
    coord_array = np.array(coordinates)
    distance_array = check_if_long_lat_array_inside_circe(
        coord_array, center_point[0], center_point[1], 2)
    filtered_list = list(compress(pakete_list, distance_array))
    pakete_coordinates = np.array(
        [(paket.lat, paket.long) for paket in filtered_list])
    kmeans = KMeans(n_clusters=4, random_state=0)
    kmeans_result = kmeans.fit_predict(pakete_coordinates)
    print(kmeans.cluster_centers_[0][1])
    pakete_with_cluster = []
    db = SessionLocal()
    for i in range(4):
        zustellbasis = db.query(Zustellbasis).get(i+1)
        zustellbasis.lat = kmeans.cluster_centers_[i][0]
        zustellbasis.long = kmeans.cluster_centers_[i][1]
        db.add(zustellbasis)
    db.commit()
    for i, (paket, cluster) in enumerate(zip(filtered_list, kmeans_result)):
        db_paket = db.query(Pakete).get(paket.id)
        db_paket.zustellbasis_id = int(cluster + 1)
        db.add(db_paket)
    db.commit()


# create_clustering()
# get_pakete()
redistribute_old_and_new_pakete_to_zustellbasen()
