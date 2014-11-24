-- Copyright (C) 2005 Institut G�ographique National
--
-- This library is free software; you can redistribute it and/or modify it under the terms
-- of the GNU Lesser General Public License as published by the Free Software Foundation; 
-- either version 2.1 of the License, or any later version.
--
-- This library is distributed in the hope that it will be useful, but WITHOUT ANY 
-- WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
-- PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public License along with 
-- this library (see file LICENSE if present); if not, write to the Free Software 
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--  
--

-- Creation de tables necessaires au fonctionnement de GeOxygene avec PostGIS et OJB.
-- Script a appliquer avant toute d'utilisation de GeOxygene avec PostGIS et OJB.

DROP TABLE DATASET;
DROP TABLE POPULATIONS;

CREATE TABLE DATASET (
    COGITID INTEGER PRIMARY KEY,
    NOM VARCHAR(255),
    TYPEBD VARCHAR(255),
    MODELE VARCHAR(255),
    ZONE VARCHAR(255),
    DATEJEU VARCHAR(255),
    COMMENTAIRE VARCHAR(255),
    APPARTIENT_A INTEGER
    );


CREATE TABLE POPULATIONS (
    COGITID INTEGER PRIMARY KEY,
    NOM VARCHAR(255),
    NOM_CLASSE_INSTANCES VARCHAR(255),
    DATASET INTEGER,
    FLAGGEOM BOOLEAN,
    FEATURETYPE integer
    );

