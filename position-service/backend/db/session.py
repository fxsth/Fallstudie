from sqlalchemy import create_engine, MetaData, Table, Column, String
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.automap import automap_base


engine_postgres = create_engine(
    "postgresql+psycopg2://postgres@localhost:5432/test")

Base = automap_base()
Base.prepare(engine_postgres, reflect=True)


Ort = Base.classes.ort
Pakete = Base.classes.pakete
Person = Base.classes.person
Zustellbasis = Base.classes.zustellbasis
Zustellbasisart = Base.classes.zustellbasisart

SessionLocal = sessionmaker(
    autocommit=False, autoflush=False, bind=engine_postgres)
