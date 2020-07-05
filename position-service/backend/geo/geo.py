import numpy as np


def haversine_np(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points
    on the earth (specified in decimal degrees)

    All args must be of equal length.

    """
    lon2, lat2 = map(np.radians, [lon2, lat2])
    lon1 = np.radians(lon1)
    lat1 = np.radians(lat1)
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = np.sin(dlat/2.0)**2 + np.cos(lat1) * np.cos(lat2) * np.sin(dlon/2.0)**2

    c = 2 * np.arcsin(np.sqrt(a))
    km = 6367 * c
    return km


def check_if_long_lat_array_inside_circe(np_array, center_long, center_lat, radius_in_km):
    distance_array = haversine_np(
        center_long, center_lat, np_array[:, 0], np_array[:, 1])
    return distance_array < radius_in_km
