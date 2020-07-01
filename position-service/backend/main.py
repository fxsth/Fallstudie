
"""
Calculate the point in time of packstation
1. Calculate the Positions in time with biasis toward being near in the timeframe



"""


from fastapi import FastAPI, Depends
from starlette.middleware.cors import CORSMiddleware
from db.session import Pakete, Ort, SessionLocal
from sqlalchemy.orm import Session
from schemas.paket import PaketRep
from sklearn.cluster import KMeans

import random
import numpy as np

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
    answer = db.query(Pakete, Ort).filter(Pakete.zustellbasis_id == None).filter(
        Pakete.wunschort_id != None).join(Ort, Pakete.wunschort_id == Ort.id).all()
    for row in answer:
        paket = row[0]
        ort = row[1]
        paket_rep = PaketRep(id=paket.id, groesse=paket.groesse,
                             fach_nummer=paket.fach_nummer, empfaenger_id=paket.empfaenger_id,  absender_id=paket.absender_id, wunschort_id=paket.wunschort_id, long=ort.long, lat=ort.lat)
        print(paket_rep)


create_clustering()
