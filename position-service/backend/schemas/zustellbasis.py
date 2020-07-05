from db.session import Zustellbasis
from pydantic import BaseModel


class ZustellbasisWithCapa(BaseModel):
    id: int
    long: float = None
    lat: float = None
    type_id: float = None
    L_N_Pakete: int = None
    M_N_Pakete: int = None
    S_N_Pakete: int = None


def create_mock_zustellbasen():
    mock_zustellbasen_list = []
    for i in range(4):
        zustell = ZustellbasisWithCapa(
            id=i, type_id=i, L_N_Pakete=5, M_N_Pakete=15, S_N_Pakete=6)
        mock_zustellbasen_list.append(zustell)
