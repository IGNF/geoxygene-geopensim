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
/**
 *
 */
package fr.ign.cogit.appli.geopensim.appli.loader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentFactory;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicRepresentationFactory;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Cimetiere;
import fr.ign.cogit.appli.geopensim.feature.micro.Parking;
import fr.ign.cogit.appli.geopensim.feature.micro.SurfaceEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TerrainSport;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconChemin;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconVoieFerree;
import fr.ign.cogit.appli.geopensim.feature.micro.Vegetation;
import fr.ign.cogit.appli.geopensim.util.GUIChargeurDonneesGeOpenSim;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;



/**
 * Chargement des données du LIV.
 * @author Julien Perret
 *
 */
public class DataLoader {
	static Logger logger=Logger.getLogger(DataLoader.class.getName());

	static int idRep = 1;
    static int idGeo = 1;
	private static Map<Integer,Map<String,AgentGeographique>> collection = new HashMap<Integer,Map<String,AgentGeographique>>();
	static int srid = -1;

	/**
	 * Charge des représentations Géographiques à partir de fichiers shape.
	 * @return une collection d'agents Géographiques dont les représentations sont chargées depuis les fichiers sélectionnés
	 */
	public static AgentGeographiqueCollection charge() {
		GUIChargeurDonneesGeOpenSim chargeur = new GUIChargeurDonneesGeOpenSim("Chargeur de données GeOpenSim",false);
		boolean validated = chargeur.showDialog();
		chargeur.dispose();
		if (!validated) {
			logger.info("Chargement annulé");
			return null;
		}
		long time = System.currentTimeMillis();
		DataSet.db=new GeodatabaseOjbPostgis();
		logger.info("Nettoyage de la base de données");
		DataSet.db.begin();
		// FIXME c'est mal mais ça marche bien :)
		DataSet.db.exeSQLFile("sql/postgis/geopensim/nettoyer_base_bdtopo.sql");
		DataSet.db.commit();
		DataSet.db.begin();
		DataSet.db.clearCache();
		DataSet.db.commit();
		List<File> shapeFiles = chargeur.getShapeFiles();
		List<Integer> shapeFilesClasses = chargeur.getShapeFilesClasses();
		String[] javaClassStrings = chargeur.getJavaClassStrings();
		Iterator<File> it_file = shapeFiles.iterator();
		Iterator<Integer> it_javaClass = shapeFilesClasses.iterator();
		AgentGeographiqueCollection geoCollection = new AgentGeographiqueCollection();
		geoCollection.setExtraction(chargeur.isExtraction());
		if (chargeur.isExtraction()) { logger.info("Les données font partie d'une extraction"); }
		srid = chargeur.getSRIDValue();
		while (it_file.hasNext() && it_javaClass.hasNext()) {
			File file = it_file.next();
			int javaClassIndex = it_javaClass.next();
			String javaClassName = javaClassStrings[javaClassIndex];
			logger.info("Chargement du fichier " + file);
			if (javaClassIndex == 0) {
				logger.warn(" --- fichier non traité : pas de classe correspondante");
				continue;
			}
			if (logger.isDebugEnabled()) {
			    logger.debug(" --- classe java correspondante " + javaClassName);
			}
			DataSet.db.begin();
			charge(file.getAbsolutePath(),javaClassName,geoCollection);
			geoCollection.makePersistent();
			DataSet.db.commit();
			System.gc();
		}
		try {
	        geoCollection.analyserElements();
	        geoCollection.rendElementsPersistants();
		} catch (Exception e) {
		    logger.info("failed to analyse");
		    return geoCollection;
		}
        try {
            geoCollection.construireHierarchies();
            geoCollection.rendElementsPersistants();
        } catch (Exception e) {
            logger.info("failed to build hierarchies");
            return geoCollection;
        }
        try {
            geoCollection.qualifier();
            geoCollection.rendElementsPersistants();
        } catch (Exception e) {
            logger.info("failed to qualify");
            return geoCollection;
        }
		//DataSet.db.begin();
		//DataSet.db.clearCache();
		//geoCollection.rendElementsPersistants();
		if (logger.isDebugEnabled()) logger.debug(geoCollection);
		//DataSet.db.commit();
		if (logger.isDebugEnabled()) logger.debug("Fin du chargement en "+(System.currentTimeMillis()-time)+" ms");
		return geoCollection;
	}

    /**
     * Chargement de représentations Géographiques à partir d'un fichier
     * shapefile à l'interieur d'une collection d'éléments Géographiques.
     * @param fileName
     *            nom du fichier à charger
     * @param javaClassName
     *            nom de la classe Java utilisée pour le chargement des objets
     *            géopgraphiques
     * @param geoCollection
     *            collection contenant les éléments Géographiques déjà chargés
     *            et dans laquelle ajouter les nouveaux.
     */
	@SuppressWarnings("unchecked")
    private static void charge(String fileName, String javaClassName,
                AgentGeographiqueCollection geoCollection) {
        BasicRepresentationFactory factory = new BasicRepresentationFactory();
        ElementRepresentation representation = factory
                    .creerElementRepresentation(javaClassName);
        if (representation == null) {
            logger.warn("Classe " + javaClassName + " non trouvée");
            return;
        }
        Class<? extends ElementRepresentation> classe = representation
                    .getClass();
        if (logger.isDebugEnabled()) {
            logger.debug("Classe trouvée " + representation.getClass());
        }
        SortedSet<Integer> dates = new TreeSet<Integer>();
        IPopulation<IFeature> population = ShapefileReader.read(fileName);
        System.gc();
        logger.info(population.size() + " features ");
        try {
            for (IFeature feature : population) {
                // essaye de Déterminer la date de la saisie à partir du champ
                // source
                String source = (String) feature.getAttribute("SOURCE");
                // logger.info("Source = "+source);
                int index = source.lastIndexOf('_');
                Integer dateInteger = -1;
                if (index < 0) index = source.length() - 4; // la chaine de
                                                            // caractère ne
                                                            // contient pas "_"
                else index++;
                if (index < 0) {
                    // la date n'est pas dans l'attribut source
                    if (feature.getAttribute("DATESOURCE") instanceof Integer) dateInteger = (Integer) feature
                                .getAttribute("DATESOURCE");
                    else dateInteger = Integer.parseInt((String) feature
                                .getAttribute("DATESOURCE"));
                } else {
                    try {
                        String dateString = source.substring(index);
                        dateInteger = Integer.parseInt(dateString);
                    } catch (NumberFormatException e) {
                        // la date n'est pas dans l'attribut source
                        try {
                            if (feature.getAttribute("DATESOURCE") instanceof Number) {
                                dateInteger = ((Number) feature
                                            .getAttribute("DATESOURCE"))
                                            .intValue();
                            } else {
                                dateInteger = Integer.parseInt((String) feature
                                            .getAttribute("DATESOURCE"));
                            }
                        } catch (Exception exception) {
                        }
                    }
                }
                // FIXME uggly fix dor collection with different dates
                if (dateInteger == 1998) { dateInteger = 2002; }
                if (dateInteger != -1) { dates.add(dateInteger); }
            }
        } catch (Exception exception) {
        }
        // If we can't find the date in the feature itself, we look for it in
        // the file name
        if (dates.isEmpty()) {
            int index = fileName.lastIndexOf('_');
            int indexExtention = fileName.lastIndexOf('.');
            if (index < 0) {
                index = fileName.length() - 4; // la chaine de caractère ne
                                               // contient pas "_"
            } else {
                index++;
            }
            String dateString = fileName.substring(index, indexExtention);
            int dateInteger = Integer.parseInt(dateString);
            dates.add(dateInteger);
        }
        int date = dates.last();
        if (dates.size() != 1) {
            logger.warn("Plusieurs dates ont été trouvées dans le fichier "
                        + fileName);
            logger
                        .warn("On affecte la dernière date trouvée à tous les objets de ce fichier : "
                                    + date);
        }
        Hashtable<String, String> mapping = new Hashtable<String, String>();
        mapping.put("ID_GEO", "IdGeo");
        mapping.put("IDGEO", "IdGeo");
        mapping.put("NATURE", "Nature");
        mapping.put("HAUTEUR", "Hauteur");
        mapping.put("SOURCE", "Source");
        mapping.put("MICRO_DET", "Nature");
        Hashtable<String, Class<?>> mappingType = new Hashtable<String, Class<?>>();
        mappingType.put("ID_GEO", AgentGeographique.class);
        mappingType.put("NATURE", String.class);
        mappingType.put("IDGEO", AgentGeographique.class);
        mappingType.put("HAUTEUR", int.class);
        mappingType.put("SOURCE", String.class);
        mappingType.put("MICRO_DET", String.class);
        List<Integer> attributeIndices = new ArrayList<Integer>();
        List<String> attributeNames = new ArrayList<String>();
        FeatureType featureType = (FeatureType) population.getFeatureType();
        for (int index = 0; index < featureType.sizeFeatureAttributes(); index++) {
            if (logger.isDebugEnabled()) logger.debug("Attribute "
                        + index + " name = "
                        + featureType.getFeatureAttributeI(index)
                                    .getMemberName() + " type = "
                        + featureType.getFeatureAttributeI(index)
                                    .getValueType());
            if (mapping.containsKey(featureType.getFeatureAttributeI(index)
                        .getMemberName())) {
                attributeIndices.add(index);
                attributeNames.add(mapping.get(featureType
                            .getFeatureAttributeI(index).getMemberName()));
                if (logger.isDebugEnabled()) logger
                            .debug(" --- attribute ajouté "
                                        + mapping
                                                    .get(featureType
                                                                .getFeatureAttributeI(
                                                                            index)
                                                                .getMemberName())
                                        + " de type "
                                        + mappingType
                                                    .get(featureType
                                                                .getFeatureAttributeI(
                                                                            index)
                                                                .getMemberName()));
            }
        }

        for (IFeature feature : population) {
            ElementRepresentation elementRepresentation = factory
                        .creerElementRepresentation(javaClassName);
            // ft_feature.setIdRep(idRep++);
            AgentGeographique geo = null;
            for (int i = 0; i < attributeIndices.size(); i++) {
                Class<?> type = mappingType.get(featureType
                            .getFeatureAttributeI(attributeIndices.get(i))
                            .getMemberName());
                // logger.info("type = "+type+" pour un attribut de type "+featureType.getFeatureAttributeI(attributeIndices.get(i)).getMemberName());
                if (type.equals(AgentGeographique.class)) {
                    // l'attribut contient l'identifiant de l'élément
                    // Géographique
                    try {
                        Object attribute = ((DefaultFeature) feature)
                                    .getAttribute(attributeIndices.get(i));
                        // logger.info(attribute);
                        int id_Geo = (attribute instanceof Number) ? ((Number) attribute)
                                    .intValue()
                                    : Integer.parseInt((String) attribute);
                        geo = getAgentGeographique(id_Geo, javaClassName,
                                    geoCollection);
                    } catch (NumberFormatException e) {
                        geo = null;
                    }
                } else {
                    Method methode;
                    try {
                        methode = classe.getMethod("set"
                                    + attributeNames.get(i), type);
                        if (type.equals(int.class)) {// l'attribut est un entier
                            logger.debug("attribut " + attributeNames.get(i));
                            Object attributeValue = ((DefaultFeature) feature)
                                        .getAttribute(attributeIndices.get(i));
                            if (attributeValue instanceof Number) {
                                methode.invoke(elementRepresentation,
                                            ((Number) attributeValue)
                                                        .intValue());
                            } else methode
                                        .invoke(
                                                    elementRepresentation,
                                                    Integer
                                                                .parseInt((String) attributeValue));
                        } else if (type.equals(String.class)) {// l'attribut est
                                                               // une chaine de
                                                               // caractère
                            methode.invoke(elementRepresentation, ((DefaultFeature) feature)
                                        .getAttribute(attributeIndices.get(i)));
                        }
                    } catch (SecurityException e) {
                        logger.warn("La Méthode \"set" + attributeNames.get(i)
                                    + "\" n'est pas accessible sur la classe "
                                    + classe);
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        logger
                                    .warn("La Méthode \"set"
                                                + attributeNames.get(i)
                                                + "\" n'existe pas sur la classe "
                                                + classe);
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        logger.warn("\""
                                    + ((DefaultFeature) feature).getAttribute(attributeIndices
                                                .get(i))
                                    + "\" n'est pas convertible en entier");
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        logger
                                    .warn("Les paramètres founis à la Méthode \"set"
                                                + attributeNames.get(i)
                                                + "\" ne sont pas du bon type sur la classe "
                                                + classe);
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // ne devrait pas être levée : la Méthode getMethod
                        // devrait lever une exception SecurityException
                        logger.warn("La Méthode \"set" + attributeNames.get(i)
                                    + "\" n'est pas accessible sur la classe "
                                    + classe);
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        logger
                                    .warn("La Méthode \"set"
                                                + attributeNames.get(i)
                                                + "\" a renvoyé une exception sur la classe "
                                                + classe);
                        e.printStackTrace();
                    }
                }
            }
            try {
                if (feature.getGeom().isMultiSurface()) {
                    if (!((GM_MultiSurface<GM_Polygon>) feature.getGeom())
                                .isEmpty()) elementRepresentation
                                .setGeom(((GM_MultiSurface<GM_Polygon>) feature
                                            .getGeom()).get(0));
                } else if (feature.getGeom().isMultiCurve()) {
                    if (!((GM_MultiCurve<?>) feature.getGeom()).isEmpty()) elementRepresentation
                                .setGeom(((GM_MultiCurve<?>) feature.getGeom())
                                            .get(0));
                } else elementRepresentation.setGeom(feature.getGeom());
                if (elementRepresentation.getGeom() != null) elementRepresentation
                            .getGeom().setCRS(srid);
            } catch (Exception e) {
                logger
                            .error("La transformation de la géométrie de l'objet en GM_Object a échouée.");
                logger.error("Aucune géométrie n'a été affectée à l'objet.");
                e.printStackTrace();
            }
            (elementRepresentation).setDateSourceSaisie(date);
            if (geo == null) {
                // on a pas d'objet géo, on en crée un ?
                geo = getAgentGeographique(idGeo, javaClassName, geoCollection);
                idGeo++;
            }
            if ((geo != null) && (elementRepresentation.getGeom() != null)) geo
                        .add(elementRepresentation);
        }
    }

	/**
	 * @param geoCollection
	 */
	/*
	private static void affiche(ElementGeoCollection geoCollection) {
		for(ElementGeo geo:geoCollection) {
			if (logger.isDebugEnabled()) logger.debug("Element Géographique "+geo.getIdGeo());
			for (ElementRepresentation rep:geo) {
				if (logger.isDebugEnabled()) logger.debug("   - "+rep.getClass()+" - "+rep.getIdRep() + " - " +((MicroRepresentation)rep).getSource()+" - "+((MicroRepresentation)rep).getDateSourceSaisie());
			}
		}
	}
	 */

	/**
	 * Récupération d'un élément Géographique existant dans la collection ou création d'un élément Géographique s'il n'existe pas.
	 * @param id_Geo identifiant de l'élément Géographique cherché
	 * @param className classe de l'élément Géographique recherché
	 * @param geoCollection collection d'éléments Géographiques
	 * @return l'élément Géographique recherché s'il n'existe, ou un nouvel élément Géographique sinon.
	 */
	private static AgentGeographique getAgentGeographique(int id_Geo, String className, AgentGeographiqueCollection geoCollection) {
		Map<String,AgentGeographique> map = collection.get(id_Geo);
		if (map==null) {
			// aucun élément Géographique avec cet identifiant idGeo
			AgentGeographique geo = AgentFactory.newAgentGeographique(toRepresentationClass(className));
			map = new HashMap<String,AgentGeographique>();
			map.put(className, geo);
			collection.put(id_Geo, map);
			geoCollection.add(geo);
			return geo;
		} else {
			AgentGeographique geo = map.get(className);
			if (geo==null) {
				geo = AgentFactory.newAgentGeographique(toRepresentationClass(className));
				map.put(className, geo);
				geoCollection.add(geo);
			}
			return geo;
		}
		//for (ElementGeo geo:geoCollection) if (geo.getIdGeo() == idGeo) return geo;
	}

	/**
	 * @param feature
	 */
	/*
	private static void affiche(ElementRepresentation feature) {
		if (logger.isDebugEnabled()) logger.debug("Feature "+feature.getIdRep());
	}
	 */

	/**
	 * @param name nom de classe
	 * @return classe java correspondante
	 */
	public static Class<?> toRepresentationClass(String name) {
		if (name.equalsIgnoreCase("Batiment")) return Batiment.class;
		if (name.equalsIgnoreCase("Cimetiere")) return Cimetiere.class;
		if (name.equalsIgnoreCase("Parking")) return Parking.class;
		if (name.equalsIgnoreCase("SurfaceEau")) return SurfaceEau.class;
		if (name.equalsIgnoreCase("TerrainSport")) return TerrainSport.class;
		if (name.equalsIgnoreCase("TronconChemin")) return TronconChemin.class;
		if (name.equalsIgnoreCase("TronconCoursEau")) return TronconCoursEau.class;
		if (name.equalsIgnoreCase("TronconRoute")) return TronconRoute.class;
		if (name.equalsIgnoreCase("TronconVoieFerree")) return TronconVoieFerree.class;
		if (name.equalsIgnoreCase("Vegetation")) return Vegetation.class;
		return null;
	}

	/**
	 * Lancement de l'interface de chargement des données du LIV.
	 * @param args pas d'arguments utilisé
	 */
	public static void main(String[] args) {
		AgentGeographiqueCollection geoCollection = charge();
		if (geoCollection == null) { return; }
		//TODO voir comment sauver proprement des dataset et des populations !

		/*
		DataSet dataset = DataSet.getInstance();
		dataset.setNom("GeOpenSim");
		dataset.setPersistant(true);
		DataSet.db.begin();
		Population<Batiment> popBatiments = new Population<Batiment> (true,"batiments",Batiment.class,true);
		dataset.addPopulation(popBatiments);
		for (PopulationUnites popUnites:geoCollection.getPopulationsUnites()) {
			popBatiments.addCollection(popUnites.getPopulationBatiments().getElements());
		}
		DataSet.db.makePersistent(dataset);
		DataSet.db.commit();
		*/
		System.exit(0);
	}

}
