from pydantic import BaseModel
from datetime import datetime, timedelta
from random import randint
import random
from .utils import ceil_dt


class PaketRep(BaseModel):
    id: int
    groesse: int
    fach_nummer: int = None
    empfaenger_id: int
    zustellbasis_id: int = None
    absender_id: int = None
    interaktion_von: datetime = None
    interaktion_bis: datetime = None
    wunschort_id: int
    long: float
    lat: float
    cluster_id: int = None

    class Config:
        orm_mode = True


def create_mock_pakete():
    mock_pakete_list = []
    for i in range(20):
        long_minutes = random.randint(-150, 330)
        lat_minutes = random.randint(-18, 18)
        lat = (5_151_000 + long_minutes) / 100000
        long = (7468 + lat_minutes) / 1_000
        start = ceil_dt(datetime.now(), timedelta(minutes=30)
                        ) + timedelta(minutes=randint(0, 45))
        end = ceil_dt(datetime.now(), timedelta(minutes=30)
                      ) + timedelta(minutes=randint(46, 100))
        paket = PaketRep(id=i, groesse=randint(
            1, 3), empfaenger_id=1, absender_id=2, wunschort_id=1, long=long, lat=lat, interaktion_von=start, interaktion_bis=end)
        mock_pakete_list.append(paket)
    return mock_pakete_list
