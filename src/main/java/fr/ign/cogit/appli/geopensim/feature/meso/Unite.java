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
package fr.ign.cogit.appli.geopensim.feature.meso;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author Julien Perret
 * 
 * Unités Géographiques composées de zones élémentaires et de zones agrégées.
 *
 */
@MappedSuperclass
public interface Unite<Elem extends ZoneElementaire, Agreg extends ZoneAgregee<Elem>> {
	/**
	 * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que
	 * pour les objets persistants
	 */
	@Id @GeneratedValue
	public abstract int getId();

	/**
	 * Affecte un identifiant (ne pas utiliser si l'objet est persistant car
	 * cela est automatique)
	 */
	public abstract void setId(int Id);
	
	public abstract IGeometry getGeom();
	public abstract void setGeom(IGeometry geom);
	
	/**
	 * @return zones élémentaires
	 */
	public abstract List<Elem> getZonesElementaires();

	/**
	 * @param elem Zones élémentaires
	 */
	public abstract void setZonesElementaires(List<Elem> elem);

	/**
	 * Ajout d'une zone élémentaire
	 * @param zone zone à ajouter
	 */
	public abstract void addZoneElementaire(Elem zone);

	/**
	 * Suppression d'une zone élémentaire
	 * @param zone zone à enlever
	 */
	public abstract void removeZoneElementaire(Elem zone);

	/**
	 * Vidage des zones élémentaires
	 */
	public abstract void emptyZonesElementaires();

	/**
	 * @return Zones agrégées
	 */
	public abstract List<Agreg> getZonesAgregees();

	/**
	 * @param agreg zones agrégées
	 */
	public abstract void setZonesAgregees(List<Agreg> agreg);
}