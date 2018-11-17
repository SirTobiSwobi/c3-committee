package org.SirTobiSwobi.c3.c3committee.core;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.SirTobiSwobi.c3.c3committee.api.TCProgress;
import org.SirTobiSwobi.c3.c3committee.db.Assignment;
import org.SirTobiSwobi.c3.c3committee.db.Athlete;
import org.SirTobiSwobi.c3.c3committee.db.CategorizationManager;
import org.SirTobiSwobi.c3.c3committee.db.Configuration;
import org.SirTobiSwobi.c3.c3committee.db.Document;
import org.SirTobiSwobi.c3.c3committee.db.Model;
import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;
import org.SirTobiSwobi.c3.c3committee.db.TrainingSession;

public class CommitteeFold extends Fold {

	public CommitteeFold(ReferenceHub refHub, long[] trainingIds, long[] evaluationIds, int foldId, long modelId,
			TrainingSession trainingSession, Trainer trainer, long configId) {
		super(refHub, trainingIds, evaluationIds, foldId, modelId, trainingSession, trainer, configId);
		// TODO Auto-generated constructor stub
	}
	
	public void run(){
		Model model=refHub.getModelManager().getModelByAddress(modelId);
		Configuration config = refHub.getConfigurationManager().getByAddress(configId);
		boolean includeImplicits = config.isIncludeImplicits(); 
		double assignmentThreshold = config.getAssignmentThreshold(); 
		Athlete[] athletes = config.getAthletes();
		
		for(int i=0; i<trainingIds.length; i++){
			for(int k=0;k<athletes.length;k++){
				Document doc = refHub.getDocumentManager().getByAddress(trainingIds[i]);
				/*
				Client client = trainer.getClient();
				WebTarget target = client.target(athletes[k].getUrl()).path("categorizations");
				Response response = target.request().post(Entity.json(doc));
				System.out.println(response.toString());
				System.out.println(response.getEntity().toString());*/
				//TCProgress progress = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(doc, MediaType.APPLICATION_JSON), TCProgress.class);
			}
			Assignment[] explicitAssignments=refHub.getTargetFunctionManager().getDocumentAssignments(trainingIds[i]);
			for(int j=0;j<explicitAssignments.length;j++){
				
			}
			
			if(includeImplicits){
				long[] implicitAssignments = refHub.getTargetFunctionManager().getImplicitCatIdsForDocument(trainingIds[i]);
			}
			model.incrementCompletedSteps();	
		}
		
		CategorizationManager evalCznMan = new CategorizationManager();
		for(int i=0; i<evaluationIds.length; i++){
			
		}
	
	}

}
