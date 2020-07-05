from datetime import datetime


def ceil_dt(dt, delta):
    return dt + (datetime.min - dt) % delta
