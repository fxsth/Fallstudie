from datetime import datetime
import pytz


def ceil_dt(dt, delta):
    dt_min = datetime.min
    dt_min = dt_min.replace(tzinfo=pytz.UTC)
    return dt + (dt_min - dt) % delta
