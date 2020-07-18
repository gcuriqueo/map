-- begin MAP_PUNTO
create table MAP_PUNTO (
    ID uuid,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer not null,
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LATITUD double precision,
    LONGITUD double precision,
    COLOR integer,
    NIVEL integer,
    --
    primary key (ID)
)^
-- end MAP_PUNTO
