-- 1. Crear la base de datos exclusiva para Keycloak
CREATE DATABASE keycloak_db;

-- 2. Conectarse a la nueva base de datos
\c keycloak_db;

-- 3. Crear el usuario para Keycloak
-- Usamos IF NOT EXISTS para que no falle en reinicios
DO
$do$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'keycloak_admin') THEN
      CREATE ROLE keycloak_admin WITH LOGIN PASSWORD 'keycloak_password';
   END IF;
END
$do$;

-- 4. Otorgar privilegios totales al esquema público para Keycloak
GRANT ALL ON SCHEMA public TO keycloak_admin;
ALTER SCHEMA public OWNER TO keycloak_admin;