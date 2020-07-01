from pydantic import BaseModel
from datetime import datetime


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

    class Config:
        orm_mode = True
