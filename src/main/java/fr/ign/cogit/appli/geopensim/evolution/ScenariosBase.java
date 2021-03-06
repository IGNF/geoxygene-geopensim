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

package fr.ign.cogit.appli.geopensim.evolution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire.Possibilites;
import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.expression.Add;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.Function;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.Multiply;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.filter.expression.Subtract;
import fr.ign.cogit.geoxygene.filter.function.FunctionImpl;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ScenariosBase {
	
	private static String cheminFichierBaseScenarios="rules/scenarios.xml";

	@XmlElementWrapper(name="Scenarios")
	@XmlElement(name="Scenario")
	List<Scenario> scenarios = new ArrayList<Scenario>();
	/**
	 * @return la valeur de l'attribut scenarios
	 */
	public List<Scenario> getScenarios() {return this.scenarios;}
	/**
	 * @param rules l'attribut scenarios à affecter
	 */
	public void setScenarios(List<Scenario> scenarios) {this.scenarios = scenarios;}

	private static ScenariosBase instance = null;
	public static ScenariosBase getInstance() {
	    synchronized(ScenariosBase.class) {
	        if (instance == null) {
	            synchronized(ScenariosBase.class) {
	                try {
                        instance = ScenariosBase.
                        unmarshall(new FileInputStream(
                                "rules/scenarios.xml"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
	            }
	        }
	    }
	    return instance;
	}
	
	public void marshall(){
		File fichier = new File(cheminFichierBaseScenarios);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fichier);
			marshall(fos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void marshall(OutputStream stream) {
		try {
			JAXBContext context = JAXBContext.newInstance(
						ScenariosBase.class,
						EvolutionRule.class,
						Expression.class,
						Multiply.class,
						Subtract.class,
						Add.class,
						Literal.class,
						PropertyName.class,
						Function.class,
						FunctionImpl.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, stream);
		} catch (JAXBException e) {e.printStackTrace();}		
	}

	public static ScenariosBase unmarshall(InputStream stream) {
		try {
			JAXBContext context = JAXBContext.newInstance(
						ScenariosBase.class,
						EvolutionRule.class,
						Expression.class,
						Multiply.class,
						Subtract.class,
						Add.class,
						Literal.class,
						PropertyName.class,
						Function.class,
						FunctionImpl.class);
			Unmarshaller m = context.createUnmarshaller();
			return (ScenariosBase) m.unmarshal(stream);
		} catch (JAXBException e) {e.printStackTrace();}
		return null;
	}

	public static void main(String[] args) {
//		ScenariosBase base = new ScenariosBase();
//		
//		
//		// Filtre
//		And and = new And();
//		PropertyIsLessThan lessThan = new PropertyIsLessThan();
//		lessThan.setPropertyName(new PropertyName("aire"));
//		lessThan.setLiteral(new Literal("48111"));
//		PropertyIsEqualTo equalTo = new PropertyIsEqualTo();
//		equalTo.setPropertyName(new PropertyName("zone"));
//		equalTo.setLiteral(new Literal("fleury"));
//		and.getOps().add(lessThan);
//		and.getOps().add(equalTo);
//		Filter[] filter = new Filter[1];
//		filter[0]=and;
//		// Possibilité
//		Possibilites possib= new Possibilites();
//		possib.probability = 0.5f;
//		possib.propertyName = "DensiteBut";
//		Multiply mul = new Multiply();
//		mul.getParameters().add(new PropertyName("densite"));
//		mul.getParameters().add(new Literal("1.05"));
//		possib.expression=mul;
//		List<Possibilites> listePossib = new ArrayList<Possibilites>();
//		listePossib.add(possib);
//		// Règles d'évolution unitaire
//		EvolutionRuleUnitaire ERU = new EvolutionRuleUnitaire();
//		ERU.setFilter(filter);
//		ERU.possib=listePossib;
//		// Liste de règles d'évolution unitaire
//		List<EvolutionRuleUnitaire> listeERU = new ArrayList<EvolutionRuleUnitaire>();
//		listeERU.add(ERU);
//		// Règle d'évolution
//		EvolutionRule rule = new EvolutionRule();
//		rule.type="ZoneElementaireUrbaine";
//		rule.start=1950;
//		rule.end=2000;
//		rule.subRules=listeERU;
//		// Liste Regles d'évolution
//		List<EvolutionRule> listeReglesEvol = new ArrayList<EvolutionRule>();
//		listeReglesEvol.add(rule);
//		// Scénario
//		Scenario scenario = new Scenario();
//		scenario.rules=listeReglesEvol;
//		scenario.Nom="Scenario1";
//		// Liste de scénarios
//		List<Scenario> listeScenarios = new ArrayList<Scenario>();
//		listeScenarios.add(scenario);
//		// Base de scénarios
//		base.setScenarios(listeScenarios);
//		base.marshall(System.out);
//		
//		try {
//			base = ScenariosBase.unmarshall(new FileInputStream("rules/scenarios.xml"));
//			base.marshall(System.out);
//		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
}
