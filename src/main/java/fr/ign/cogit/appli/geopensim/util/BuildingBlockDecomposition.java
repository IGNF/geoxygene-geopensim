/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentFactory;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author Julien Perret
 *
 */
public class BuildingBlockDecomposition {
    static Logger logger = Logger.getLogger(BuildingBlockDecomposition.class);
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        int date1 = 1989;
        int date2 = 2002;
        String outputDirectory = "D:\\Users\\JulienPerret\\Data\\Strasbourg\\DecomposedPrediction\\"+date1+"-"+date2+"\\";
        CarteTopo carte = new CarteTopo("Carte");

        IPopulation<IFeature> inputBuildingsDate1 = null;
        IPopulation<IFeature> inputBuildingsDate2 = null;

        //String[] zones = {"Z1", "Z2", "Z3", "Z4"};
        String[] zones = {""};
        for (String zone : zones) {
            //String inputDirectory = "D:\\Users\\JulienPerret\\Data\\Strasbourg\\IGN_"+zone+"\\";
            String inputDirectory = "D:\\Users\\JulienPerret\\Data\\CUS_1989_2002\\";
            // network files for date 1
            /*
            String[] networkFilesDate1 = {
                    inputDirectory + "troncon_chemin_"+zone+"\\troncon_chemin_"+date1+"_"+zone+".shp",
                    inputDirectory + "troncon_cours_eau_"+zone+"\\troncon_cours_eau_"+date1+"_"+zone+".shp",
                    inputDirectory + "troncon_voie_ferree_"+zone+"\\troncon_voie_ferree_"+date1+"_"+zone+".shp",
                    inputDirectory + "troncon_route_"+zone+"\\troncon_route_"+date1+"_"+zone+".shp"};
                    */
            String[] networkFilesDate1 = {
                    inputDirectory + "CUS_"+date1+"\\cus_autre_voie_com_"+date1+".shp",
                    inputDirectory + "CUS_"+date1+"\\cus_troncon_hydro_"+date1+".shp",
                    inputDirectory + "CUS_"+date1+"\\cus_troncon_voie_ferree_"+date1+".shp",
                    inputDirectory + "CUS_"+date1+"\\cus_troncon_route_"+date1+".shp"};
            // building files for date 1
            //String buildingFileDate1 = inputDirectory + "batiment_"+zone+"\\batiment_"+date1+"_"+zone+".shp";
            String buildingFileDate1 = inputDirectory + "CUS_"+date1+"\\cus_batiment_"+date1+".shp";
            // network files for date 2
            /*
            String[] networkFilesDate2 = {
                    inputDirectory + "troncon_chemin_"+zone+"\\troncon_chemin_"+date2+"_"+zone+".shp",
                    inputDirectory + "troncon_cours_eau_"+zone+"\\troncon_cours_eau_"+date2+"_"+zone+".shp",
                    inputDirectory + "troncon_voie_ferree_"+zone+"\\troncon_voie_ferree_"+date2+"_"+zone+".shp",
                    inputDirectory + "troncon_route_"+zone+"\\troncon_route_"+date2+"_"+zone+".shp"};
                    */
            String[] networkFilesDate2 = {
                    inputDirectory + "CUS_"+date2+"\\TRONCON_CHEMIN_"+date2+".shp",
                    inputDirectory + "CUS_"+date2+"\\TRONCON_COURS_EAU_"+date2+".shp",
                    inputDirectory + "CUS_"+date2+"\\TRONCON_VOIE_FERREE_"+date2+".shp",
                    inputDirectory + "CUS_"+date2+"\\TRONCON_ROUTE_"+date2+".shp"};
            // building files for date 2
            //String buildingFileDate2 = inputDirectory + "batiment_"+zone+"\\batiment_"+date2+"_"+zone+".shp";
            String buildingFileDate2 = inputDirectory + "CUS_"+date2+"\\BATIMENT_"+date2+".shp";

            for (String file : networkFilesDate1) {
                try {
                    IPopulation<IFeature> features = ShapefileReader.read(file);
                    carte.importClasseGeo(features);
                } catch (Exception e) {
                }
            }
            /*
            for (String file : networkFilesDate2) {
                try {
                    Population<DefaultFeature> features = ShapefileReader.read(file);
                    carte.importClasseGeo(features);
                } catch (Exception e) {
                }
            }
            */
            if (inputBuildingsDate1 == null) {
                inputBuildingsDate1 = ShapefileReader.read(buildingFileDate1);
            } else {
                inputBuildingsDate1.addAll(ShapefileReader.read(buildingFileDate1));
            }
            if (inputBuildingsDate2 == null) {
                inputBuildingsDate2 = ShapefileReader.read(buildingFileDate2);
            } else {
                inputBuildingsDate2.addAll(ShapefileReader.read(buildingFileDate2));
            }
        }
        if (logger.isDebugEnabled()) logger.debug("--- creation des noeuds --- ");
        carte.creeNoeudsManquants(1.0);

        if (logger.isDebugEnabled()) logger.debug("--- fusion des noeuds --- ");
        carte.fusionNoeuds(1.0);

        if (logger.isDebugEnabled()) logger.debug("--- découpage des arcs --- ");
        carte.decoupeArcs(1.0);

        if (logger.isDebugEnabled()) logger.debug("--- filtrage des arcs doublons --- ");
        carte.filtreArcsDoublons();

        if (logger.isDebugEnabled()) logger.debug("--- rend planaire --- ");
        carte.rendPlanaire(1.0);

        if (logger.isDebugEnabled()) logger.debug("--- fusion des doublons --- ");
        carte.fusionNoeuds(1.0);

        if (logger.isDebugEnabled()) logger.debug("--- filtrage des arcs doublons --- ");
        carte.filtreArcsDoublons();

        if (logger.isDebugEnabled()) logger.debug("--- creation de la topologie des Faces --- ");
        carte.creeTopologieFaces();

        logger.info(carte.getListeFaces().size()+" faces trouvées");
        
        if (logger.isDebugEnabled()) logger.debug("Création de l'Index spatial");
        carte.getPopFaces().initSpatialIndex(Tiling.class,false);

        logger.info("Index spatial initialisé");

        Population<AgentGeographique> agents = new Population<AgentGeographique>();
        Population<ZoneElementaireUrbaine> ilotsDate1 = new Population<ZoneElementaireUrbaine>();
        Population<ZoneElementaireUrbaine> ilotsDate2 = new Population<ZoneElementaireUrbaine>();
        Population<Batiment> buildingsDate1 = new Population<Batiment>();
        Population<Batiment> buildingsDate2 = new Population<Batiment>();
        // parcours des faces et création des ilots décomposés
        for (Face face : carte.getPopFaces()) {
            ZoneElementaireUrbaine zoneElementaire1 = ZoneElementaireUrbaine
            .newInstance(face.getGeometrie());
            zoneElementaire1.setDateSourceSaisie(date1);
            zoneElementaire1.setInfinite(face.isInfinite());
            // ajout des bâtiments
            addBuildings(zoneElementaire1, inputBuildingsDate1, buildingsDate1, date1);
            zoneElementaire1.qualifier();
            ZoneElementaireUrbaine zoneElementaire2 = ZoneElementaireUrbaine
            .newInstance(face.getGeometrie());
            zoneElementaire2.setDateSourceSaisie(date2);
            zoneElementaire2.setInfinite(face.isInfinite());
            // ajout des bâtiments
            addBuildings(zoneElementaire2, inputBuildingsDate2, buildingsDate2, date2);
            zoneElementaire2.qualifier();
            AgentGeographique agent = AgentFactory.newAgentGeographique(ZoneElementaireUrbaine.class);
            agent.add(zoneElementaire1);
            agent.add(zoneElementaire2);
            agents.add(agent);
            ilotsDate1.add(zoneElementaire1);
            ilotsDate2.add(zoneElementaire2);
            // max med min
        }
        FeatureType buildingBlockFeatureType = new FeatureType();
        buildingBlockFeatureType.setGeometryType(GM_Polygon.class);
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("densite", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("idGeo", "integer"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("nbbat", "nombreBatiments", "integer"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("elongation", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("convexite", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("aire", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("moyairebat", "MoyenneAiresBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("ectairebat", "EcartTypeAiresBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("maxairebat", "MaxAiresBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("minairebat", "MinAiresBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("medairebat", "MedianeAiresBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("moyelonbat", "MoyenneElongationBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("ectelonbat", "EcartTypeElongationBatiments", "double"));
//        featureType.addFeatureAttribute(new AttributeType("maxelonbat", "double"));
//        featureType.addFeatureAttribute(new AttributeType("minelonbat", "double"));
//        featureType.addFeatureAttribute(new AttributeType("medelonbat", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("moyconvbat", "MoyenneConvexiteBatiments", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("ectconvbat", "EcartTypeElongationBatiments", "double"));
//        featureType.addFeatureAttribute(new AttributeType("maxconvbat", "double"));
//        featureType.addFeatureAttribute(new AttributeType("minconvbat", "double"));
//        featureType.addFeatureAttribute(new AttributeType("medconvbat", "double"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("classifonc", "ClassificationFonctionnelle", "integer"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("datesource", "DateSourceSaisie", "integer"));
        buildingBlockFeatureType.addFeatureAttribute(new AttributeType("Infinite", "boolean"));
        ilotsDate1.setFeatureType(buildingBlockFeatureType);
        ilotsDate2.setFeatureType(buildingBlockFeatureType);
        logger.info("Saving urban blocks");
        ShapefileWriter.write(ilotsDate1, outputDirectory + "ilots_"+date1+".shp");
        ShapefileWriter.write(ilotsDate2, outputDirectory + "ilots_"+date2+".shp");
        FeatureType buildingFeatureType = new FeatureType();
        buildingFeatureType.setGeometryType(GM_Polygon.class);
        buildingFeatureType.addFeatureAttribute(new AttributeType("zoneElem", "integer"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("aire", "double"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("convexite", "double"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("elongation", "double"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("biscornu", "biscornuite", "string"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("or_gene", "OrientationGenerale", "double"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("or_murs", "OrientationCotes", "double"));
        //buildingFeatureType.addFeatureAttribute(new AttributeType("or_route", "OrientationGeneraleRoute", "double"));
        //buildingFeatureType.addFeatureAttribute(new AttributeType("or_m_route", "OrientationMursRoute", "double"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("nature", "String"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("datesource", "DateSourceSaisie", "integer"));
        buildingFeatureType.addFeatureAttribute(new AttributeType("type", "typeFonctionnel", "integer"));
        buildingsDate1.setFeatureType(buildingFeatureType);
        buildingsDate2.setFeatureType(buildingFeatureType);
        logger.info("Saving buildings");
        ShapefileWriter.write(buildingsDate1, outputDirectory + "buildings_"+date1+".shp");
        ShapefileWriter.write(buildingsDate2, outputDirectory + "buildings_"+date2+".shp");
        /*
        logger.info("Saving edges in " + outputDirectory + "arcs.shp");
        ShapefileWriter.write(carte.getPopArcs(), outputDirectory + "arcs.shp");
        logger.info("Saving nodes in " + outputDirectory + "noeuds.shp");
        ShapefileWriter.write(carte.getPopNoeuds(), outputDirectory + "noeuds.shp");
        logger.info("Saving faces in " + outputDirectory + "faces.shp");
        ShapefileWriter.write(carte.getPopFaces(), outputDirectory + "faces.shp");
        */
    }
    /**
     * @param buildingBlock
     * @param inputBuildingPopulation
     * @param buildingsPopulation 
     */
    @SuppressWarnings("unchecked")
    private static void addBuildings(ZoneElementaireUrbaine buildingBlock,
            IPopulation<IFeature> inputBuildingPopulation, IPopulation<Batiment> buildingsPopulation, int date) {
        Collection<IFeature> buildingsInBuildingBlockDate1 = inputBuildingPopulation.select(buildingBlock.getGeometrie());
        Collection<Batiment> buildings = new ArrayList<Batiment>(0);
        for (IFeature buildingFeature : buildingsInBuildingBlockDate1) {
            IGeometry geometry = null;
            if (buildingBlock.getGeometrie().contains(buildingFeature.getGeom())) {
                geometry = buildingFeature.getGeom();
            } else {
                // we need to decompose the building
                geometry = buildingFeature.getGeom().intersection(buildingBlock.getGeom());
            }
            if (geometry.area() > 1) {
                String nature = (String) buildingFeature.getAttribute((date == 1989) ? "TYPE" : "CATEGORIE");
                /*
                if (nature == null) {
                    nature = (String) buildingFeature.getAttribute("CATEGORIE");
                }
                */
                if (geometry.isPolygon()) {
                    BasicBatiment building = new BasicBatiment();
                    building.setGeom(geometry);
                    building.setDateSourceSaisie(buildingBlock.getDateSourceSaisie());
                    //building.setNature((String) buildingFeature.getAttribute("MICRO_DET"));
                    building.setNature(nature);
//                    buildingBlock.addBatiment(building);
                    buildings.add(building);
                    buildingsPopulation.add(building);
                } else {
                    if (geometry.isMultiSurface()) {
                        for (GM_Polygon polygon : ((GM_MultiSurface<GM_Polygon>) geometry).getList()) {
                            if (polygon.area() > 1) {
                                BasicBatiment building = new BasicBatiment();
                                building.setGeom(polygon);
                                building.setDateSourceSaisie(buildingBlock.getDateSourceSaisie());
                                //building.setNature((String) buildingFeature.getAttribute("MICRO_DET"));
                                building.setNature(nature);
//                                buildingBlock.addBatiment(building);
                                buildingsPopulation.add(building);
                            }
                        }
                    }
                }
            }
        }
        buildingBlock.construireGroupes(buildings);
    }
}
