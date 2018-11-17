package org.SirTobiSwobi.c3.c3committee.core;

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.SirTobiSwobi.c3.c3committee.api.TCCategorization;
import org.SirTobiSwobi.c3.c3committee.api.TCCategorizations;
import org.SirTobiSwobi.c3.c3committee.api.TCModel;
import org.SirTobiSwobi.c3.c3committee.db.Assignment;
import org.SirTobiSwobi.c3.c3committee.db.Athlete;
import org.SirTobiSwobi.c3.c3committee.db.Categorization;
import org.SirTobiSwobi.c3.c3committee.db.CategorizationManager;
import org.SirTobiSwobi.c3.c3committee.db.Configuration;
import org.SirTobiSwobi.c3.c3committee.db.Document;
import org.SirTobiSwobi.c3.c3committee.db.Evaluation;
import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;
import org.SirTobiSwobi.c3.c3committee.db.TrainingSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jackson.Jackson;

public class AthleteThread extends Thread {
	private ReferenceHub refHub;
	private Athlete athlete;
	private ArrayList<Categorization> categorizations;
	private long[] trainingIds;
	private Configuration config;
	private Trainer trainer;
	private TrainingSession trainingSession;
	
	
	public AthleteThread(ReferenceHub refHub, Athlete athlete, ArrayList<Categorization> categorizations, long[] trainingIds,
			Configuration config, Trainer trainer, TrainingSession trainingSession) {
		super();
		this.refHub = refHub;
		this.athlete = athlete;
		this.categorizations = categorizations;
		this.trainingIds = trainingIds;
		this.config = config;
		this.trainer = trainer;
		this.trainingSession = trainingSession;
	}


	public void run() {
		Client client = new JerseyClientBuilder(trainer.getEnvironment()).using(trainer.getC3config().getJerseyClientConfiguration()).build(getName());
		CategorizationManager evalCznMan = new CategorizationManager();
		for(int i=0; i<trainingIds.length; i++){
			Document doc = refHub.getDocumentManager().getByAddress(trainingIds[i]);			
			WebTarget target = client.target(athlete.getUrl()).path("categorizations");
			Response response = target.request().post(Entity.json(doc));
			System.out.println(response.toString());
			String responseBody = response.readEntity(String.class);
			System.out.println(responseBody);
		}
		for(int i=0; i<trainingIds.length; i++){
			Document doc = refHub.getDocumentManager().getByAddress(trainingIds[i]);
			WebTarget target = client.target(athlete.getUrl()).path("categorizations").path("documents").path(""+doc.getId());
			Response response = target.request().get();
			System.out.println(response.toString());
			String responseBody = response.readEntity(String.class);
			System.out.println(responseBody);
			ObjectMapper MAPPER = Jackson.newObjectMapper();
			try {
				TCCategorizations retrieved =  MAPPER.readValue(responseBody, TCCategorizations.class);
				for(int j=0; j<retrieved.getCategorizations().length;j++){
					TCCategorization cat = retrieved.getCategorizations()[j];
					categorizations.add(new Categorization(cat.getId(), cat.getDocumentId(), cat.getCategoryId(), cat.getProbability()));
					evalCznMan.addCategorizationWithoutId(cat.getDocumentId(), cat.getCategoryId(), cat.getProbability());
					System.out.println(categorizations.get(categorizations.size()-1).getDocumentId()+" "+
							categorizations.get(categorizations.size()-1).getCategoryId());
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		client.close();
		
		Assignment[] relevantAssignments=null;
		for(int i=0; i<trainingIds.length; i++){
			relevantAssignments = Utilities.arrayUnionWithoutDuplicates(relevantAssignments, refHub.getTargetFunctionManager().getDocumentAssignments(trainingIds[i])); 
		}
		String evalDescription = "Athlete: "+athlete.getId();
		Evaluation eval = new Evaluation(	relevantAssignments, 
				evalCznMan.getCategorizationArray(), 
				refHub.getCategoryManager().getCategoryArray(), 
				refHub.getCategoryManager().getRelationshipArray(), 
				refHub.getDocumentManager().getDocumentArray(),  
				evalDescription,
				config.isIncludeImplicits(), 
				config.getAssignmentThreshold(),
				trainingSession,
				(int)athlete.getId());
		trainer.computeWeights();
	}
	
	
	
}
