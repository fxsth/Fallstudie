from geo.geo import check_if_long_lat_array_inside_circe
from schemas.paket import create_mock_pakete
import numpy as np
from itertools import compress
from sklearn.cluster import KMeans
from schemas.utils import ceil_dt
from datetime import datetime, timedelta


def test_geo():
    pakete_list = create_mock_pakete()
    start = ceil_dt(datetime.now(), timedelta(minutes=30)
                    )
    end = ceil_dt(datetime.now(), timedelta(minutes=30)
                  ) + timedelta(minutes=(30))
    print(f"Next timeslot: {start}")

    print(f"Alle Pakete ohne Packstation: {len(pakete_list)}")
    pakete_for_next_timeslot = []
    for paket in pakete_list:
        if (paket.zustellbasis_id is not None) or (start <= paket.interaktion_von <= end):
            pakete_for_next_timeslot.append(paket)
    print(f"Alle Pakete für den Timeslot: {len(pakete_for_next_timeslot)}")
    coordinates = [(paket.lat, paket.long) for paket in pakete_list]
    center_point = (51.514244, 7.468429)
    test_array = np.array(coordinates)
    distance_array = check_if_long_lat_array_inside_circe(
        test_array, center_point[0], center_point[1], 2)
    filtered_list = list(compress(pakete_list, distance_array))
    assert len(filtered_list) == np.sum(distance_array)
    pakete_coordinates = np.array(
        [(paket.lat, paket.long) for paket in filtered_list])
    kmeans_result = KMeans(
        n_clusters=4, random_state=0).fit_predict(pakete_coordinates)
    pakete_with_cluster = []
    for paket, cluster in zip(filtered_list, kmeans_result):
        paket.cluster_id = cluster
        pakete_with_cluster.append(paket)
    for i in range(1, 4):
        pass


def find_into_packstationen():
    pass


test_geo()
