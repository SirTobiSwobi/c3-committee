package org.SirTobiSwobi.c3.c3committee.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.SirTobiSwobi.c3.c3committee.api.TCCategorization;
import org.SirTobiSwobi.c3.c3committee.api.TCCategorizations;
import org.SirTobiSwobi.c3.c3committee.db.Athlete;
import org.SirTobiSwobi.c3.c3committee.db.Categorization;
import org.SirTobiSwobi.c3.c3committee.db.Category;
import org.SirTobiSwobi.c3.c3committee.db.Document;
import org.SirTobiSwobi.c3.c3committee.db.Model;
import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

public class CommitteeThread extends CategorizationThread {
	private Client client;

	public CommitteeThread(ReferenceHub refHub, long docId, Client client) {
		super(refHub, docId);
		this.client=client;
		// TODO Auto-generated constructor stub
	}
	
	public void run(){
		Category[] categories = refHub.getCategoryManager().getCategoryArray();
		Model model = refHub.getActiveModel();
		ArrayList<Categorization> categorizations = CommitteeThread.performCategorization(document, categories, model, client);
		for(int i=0;i<categorizations.size();i++){
			refHub.getCategorizationManager().addCategorizationWithoutId(categorizations.get(i).getDocumentId(), 
					categorizations.get(i).getCategoryId(), categorizations.get(i).getProbability(), categorizations.get(i).getExplanation());
		}
	}
	
	public static ArrayList<Categorization> performCategorization(Document doc, Category[] categories, Model model, Client client){
		ArrayList<Categorization> categorizations= new ArrayList<Categorization>();
		Athlete[] athletes = model.getConfiguration().getAthletes();
		double[][] probabilities = new double[categories.length][athletes.length];
		double[] weights = model.getWeights();
		for(int i=0; i<athletes.length; i++){
			Athlete athlete = athletes[i];
			for(int j=0;j<probabilities.length;j++){
				probabilities[j][i]=0.0;
			}
			
			WebTarget target = client.target(athlete.getUrl()).path("categorizations");
			Response response = target.request().post(Entity.json(doc));
			
			System.out.println(response.toString());
			String responseBody = response.readEntity(String.class);
			System.out.println(responseBody);
			
			target = client.target(athlete.getUrl()).path("categorizations").path("documents").path(""+doc.getId());
			response = target.request().get();
			responseBody = response.readEntity(String.class);
			System.out.println(responseBody);
			ObjectMapper MAPPER = Jackson.newObjectMapper();
			try {
				TCCategorizations retrieved =  MAPPER.readValue(responseBody, TCCategorizations.class);
				for(int j=0; j<retrieved.getCategorizations().length;j++){
					TCCategorization cat = retrieved.getCategorizations()[j];
					probabilities[Utilities.getIndexOfCatIdInArray(categories, cat.getCategoryId())][i] = cat.getProbability()*weights[i];
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
		
		for(int i=0;i<probabilities.length;i++){
			double sumCategoryLikelyhood = 0.0;
			for(int j=0; j<athletes.length;j++){
				sumCategoryLikelyhood+=probabilities[i][j];
			}
			if(sumCategoryLikelyhood>=model.getConfiguration().getAssignmentThreshold()){
				String explanation="The committee assigned the document to this category because the sum of all reported likelihoods "+sumCategoryLikelyhood
						+ " to belong to this category was higher than the assignment threshold "+model.getConfiguration().getAssignmentThreshold()+". Used classifiers: ";
				for(int j=0;j<athletes.length;j++){
					explanation+=athletes[j].getUrl()+" ";
				}
				Categorization cat = new Categorization(-1, doc.getId(), categories[i].getId(),sumCategoryLikelyhood, explanation);
				categorizations.add(cat);
			}
		}
		
		return categorizations;
	}

}
