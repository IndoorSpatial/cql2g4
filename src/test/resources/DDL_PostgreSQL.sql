DROP TABLE t;
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE OR REPLACE FUNCTION avg(speed TEXT)
RETURNS FLOAT AS $$
BEGIN
RETURN 1;
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION Buffer(
	geom GEOMETRY(POLYGON, 4326),
	width FLOAT,
	suffix TEXT
)
RETURNS GEOMETRY(POINT, 4326) AS $$
BEGIN
RETURN ST_GeomFromText('POINT (0 0)');
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION Foo(geom GEOMETRY(POLYGON, 4326))
RETURNS BOOLEAN AS $$
BEGIN
RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION Bar(
	geom GEOMETRY(POLYGON, 4326),
	num INT,
	a TEXT,
	b TEXT,
	valid BOOLEAN
)
RETURNS BOOLEAN AS $$
BEGIN
RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
CREATE TABLE t (
   "eo:cloud_cover" INT,
   "value" INT,
   "foo" INT,
   "bar" INT,
   "category" INT,

   "x" FLOAT,
   "balance" FLOAT,
   "floors" FLOAT,
   "taxes" FLOAT,
   "depth" FLOAT,
   "vehicle_height" FLOAT,
   "bridge_clearance" FLOAT,

   "id" TEXT,
   "name" TEXT,
   "landsat:scene_id" TEXT,
   "landsat:wrs_path" INT,
   "eo:instrument" TEXT,
   "beamMode" TEXT,
   "city" TEXT,
   "owner" TEXT,
   "material" TEXT,
   "etat_vol" TEXT,
   "cityName" TEXT,
   "road_class" TEXT,
   "geophys:SURVEY_NAME" TEXT,
   "windSpeed" TEXT,

   "swimming_pool" BOOL,

   "updated" DATE,
   "built" DATE,
   "updated_at" DATE,
   "event_time" DATE,
   "starts_at" DATE,
   "ends_at" DATE,
   "touchdown" DATE,
   "liftOff" DATE,

   "footprint" GEOMETRY(POLYGON, 4326),
   "location" GEOMETRY(POLYGON, 4326),
   "geometry" GEOMETRY(POLYGON, 4326),
   "road" GEOMETRY(POLYGON, 4326),

   "values" TEXT[],
   "layer:ids" TEXT[]
);