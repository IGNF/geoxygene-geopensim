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
package fr.ign.cogit.appli.geopensim.comportement;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.Agent;


/**
 * @author Julien Perret
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Comportement")
public class Comportement {
	private static Logger logger=Logger.getLogger(Comportement.class.getName());
	
	@XmlTransient
	protected int id;
    /**
     * @return
     */
    @Id @GeneratedValue
    public int getId() {return id;}
    /**
     * @param id
     */
    public void setId(int id) {this.id=id;}

	/**
	 * @param agent
	 */
	public void declencher(Agent agent) {
	    if (logger.isDebugEnabled()) logger.debug("déclenchement d'un comportement sur l'agent "+agent.getClass().getSimpleName()+" "+agent.getId());
	}
}
