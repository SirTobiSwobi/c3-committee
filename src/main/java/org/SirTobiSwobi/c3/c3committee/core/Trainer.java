package org.SirTobiSwobi.c3.c3committee.core;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.client.Client;

import org.SirTobiSwobi.c3.c3committee.C3CommitteeConfiguration;
import org.SirTobiSwobi.c3.c3committee.db.Assignment;
import org.SirTobiSwobi.c3.c3committee.db.Categorization;
import org.SirTobiSwobi.c3.c3committee.db.CategorizationManager;
import org.SirTobiSwobi.c3.c3committee.db.Category;
import org.SirTobiSwobi.c3.c3committee.db.Configuration;
import org.SirTobiSwobi.c3.c3committee.db.Document;
import org.SirTobiSwobi.c3.c3committee.db.Evaluation;
import org.SirTobiSwobi.c3.c3committee.db.Model;
import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;
import org.SirTobiSwobi.c3.c3committee.db.SelectionPolicy;
import org.SirTobiSwobi.c3.c3committee.db.TrainingSession;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;

public class Trainer {
	private ReferenceHub refHub;
	private int openEvaluations;
	private long trainingSessionId;
	private long modelId;
	private long configId;
	private Environment environment;
	private C3CommitteeConfiguration c3config;
	private long[] allIds;
	private Client client;
	
	public Trainer(ReferenceHub refHub) {
		super();
		this.refHub = refHub;
	}
	
	private long[] computeTrainingIdsFromEvaluationIds(long[] allIds, long[] evaluationIds){
		long [] output = new long[allIds.length-evaluationIds.length];
		ArrayList<Long> allIdsList = new ArrayList<Long>();
		ArrayList<Long> evaluationIdsList = new ArrayList<Long>();
		for(int i=0;i<allIds.length;i++){
			allIdsList.add(allIds[i]);
		}
		for(int i=0;i<evaluationIds.length;i++){
			evaluationIdsList.add(evaluationIds[i]);
		}
		allIdsList.removeAll(evaluationIdsList);
		for(int i=0;i<output.length;i++){
			output[i]=allIdsList.get(i);
		}
		return output;
	}

	public synchronized void startTraining(long configId, long modelId){
		this.configId = configId;
		refHub.getModelManager().setTrainingInProgress(true);
		Configuration config = refHub.getConfigurationManager().getByAddress(configId);
		int folds = config.getFolds();
		//this.openEvaluations=folds;
		this.openEvaluations=config.getAthletes().length;
		this.modelId=modelId; // There is always only one active training session per microservice. 
		Assignment[] assignments = refHub.getTargetFunctionManager().getAssignmentArray();
		ArrayList<Long> relevantDocIds = new ArrayList<Long>();
		for(int i=0; i<assignments.length; i++){
			long id = assignments[i].getDocumentId();
			if(!relevantDocIds.contains(id)){
				relevantDocIds.add(id);
			}
		}
		
		
		/*
		Document[] allDocs=refHub.getDocumentManager().getDocumentArray();
		int overallSteps = allDocs.length*folds;
		refHub.getModelManager().getModelByAddress(modelId).setSteps(overallSteps);
		*/
		trainingSessionId = refHub.getEvaluationManager().addTrainingSessionWithoutId(modelId, "");
		TrainingSession trainingSession = refHub.getEvaluationManager().getTrainingSessionByAddress(trainingSessionId);
		/*
		long[] allIds = new long[allDocs.length];
		for(int i=0;i<allIds.length;i++){
			allIds[i]=allDocs[i].getId();
		}
		*/
		allIds = new long[relevantDocIds.size()];
		int overallSteps = allIds.length*2*config.getAthletes().length+allIds.length;
		refHub.getModelManager().getModelByAddress(modelId).setSteps(overallSteps);
		
		for(int i=0;i<allIds.length;i++){
			allIds[i]=relevantDocIds.get(i);
		}
		ArrayList<ArrayList<Categorization>> categorizations = new ArrayList<ArrayList<Categorization>>();
		for(int i=0; i<config.getAthletes().length;i++){
			ArrayList<Categorization> categorization = new ArrayList<Categorization>();
			categorizations.add(categorization);
			(new AthleteThread(refHub, config.getAthletes()[i],categorization,allIds,config,this, trainingSession)).start();
		}
		
		/*
		for(int i=0; i<folds;i++){
			int start=(allIds.length/folds)*i;
			int end=((allIds.length/folds)*(i+1));
			if(i==folds-1){
				end=allIds.length;
			}
			
			//long[] evaluationIds = Arrays.copyOfRange(allIds, start, end);
			long[] evaluationIds = computeModularEvaluationIds(allIds, folds, i);
			long[] trainingIds = computeTrainingIdsFromEvaluationIds(allIds,evaluationIds);
			
			(new CommitteeFold(refHub, trainingIds, evaluationIds, i, modelId, trainingSession, this, configId)).start();
		}	
		*/
	}
	
	private long[] computeModularEvaluationIds(long[] allIds, int folds, int fold){
		ArrayList<Long> relevantIds=new ArrayList<Long>();
		for(int i=0;i<allIds.length;i++){
			if(allIds[i]%folds==fold){
				relevantIds.add(allIds[i]);
			}
		}
		long[] evaluationIds = new long[relevantIds.size()];
		for(int i=0;i<evaluationIds.length; i++){
			evaluationIds[i]=relevantIds.get(i);
		}
		return evaluationIds;
	}
	
	public synchronized void computeWeights(){
		openEvaluations--;
		if(openEvaluations==0){
			TrainingSession trainingSession = refHub.getEvaluationManager().getTrainingSessionByAddress(trainingSessionId);
			String appendString="";
			Model model = refHub.getModelManager().getModelByAddress(modelId);
			SelectionPolicy selectionPolicy = refHub.getConfigurationManager().getByAddress(configId).getSelectionPolicy();
			Evaluation[] evaluations = trainingSession.getEvaluationArray();
			model.setWeights(new double[evaluations.length]);
			for(int i=0;i<evaluations.length;i++){
				Evaluation eval = evaluations[i];
				if(selectionPolicy==SelectionPolicy.MicroaverageF1){
					model.getWeights()[i]=eval.getMicroaverageF1();
				}else if(selectionPolicy==SelectionPolicy.MicroaveragePrecision){
					model.getWeights()[i]=eval.getMicroaveragePrecision();
				}else if(selectionPolicy==SelectionPolicy.MicroaverageRecall){
					model.getWeights()[i]=eval.getMicroaverageRecall();
				}else if(selectionPolicy==SelectionPolicy.MacroaverageF1){
					model.getWeights()[i]=eval.getMacroaverageF1();
				}else if(selectionPolicy==SelectionPolicy.MacroaveragePrecision){
					model.getWeights()[i]=eval.getMacroaveragePrecision();
				}else if(selectionPolicy==SelectionPolicy.MacroaverageRecall){
					model.getWeights()[i]=eval.getMacroaverageRecall();
				}
			}
			model.setWeights(Utilities.sumVectorOne(model.getWeights()));
			model.setTrainingSetSize(this.allIds.length);
			CategorizationManager evalCznMan = new CategorizationManager();
			for(int i=0;i<allIds.length;i++){
				Document doc = refHub.getDocumentManager().getByAddress(allIds[i]);
				ArrayList<Categorization> categorizations = CommitteeThread.performCategorization(doc, refHub.getCategoryManager().getCategoryArray(), model, client);
				for(int j=0;j<categorizations.size();j++){
					evalCznMan.addCategorizationWithoutId(categorizations.get(j).getDocumentId(), categorizations.get(j).getCategoryId(), categorizations.get(j).getProbability(), "");
				}
				model.incrementCompletedSteps();
			}
			
			Assignment[] relevantAssignments=null;
			for(int i=0; i<allIds.length; i++){
				relevantAssignments = Utilities.arrayUnionWithoutDuplicates(relevantAssignments, refHub.getTargetFunctionManager().getDocumentAssignments(allIds[i])); 
			}
			Configuration config = refHub.getConfigurationManager().getByAddress(configId);
			//trainingSession.setFoldEvaluations(new ArrayList<Evaluation>());
			Evaluation eval = new Evaluation(	relevantAssignments, 
					evalCznMan.getCategorizationArray(), 
					refHub.getCategoryManager().getCategoryArray(), 
					refHub.getCategoryManager().getRelationshipArray(), 
					refHub.getDocumentManager().getDocumentArray(),  
					"Final evaluation of all utilized athletes",
					config.isIncludeImplicits(), 
					config.getAssignmentThreshold(),
					trainingSession,
					config.getFolds());			
			refHub.getModelManager().setTrainingInProgress(false);
		}
	}
	
	public synchronized void selectBestEvaluation(){
		/*
		 * Implementing a semaphore so that the selection only takes place when all folds have been computed.
		 */
		openEvaluations--;
		if(openEvaluations==0){
			TrainingSession trainingSession = refHub.getEvaluationManager().getTrainingSessionByAddress(trainingSessionId);
			String appendString="";
			Model model = refHub.getModelManager().getModelByAddress(modelId);
			SelectionPolicy selectionPolicy = refHub.getConfigurationManager().getByAddress(configId).getSelectionPolicy();
			Evaluation[] evaluations = trainingSession.getEvaluationArray();	
			model.appendToTrainingLog("There are "+evaluations.length+" evaluations.");
			double maxValue=0.0;
			int maxId=0;
			for(int i=0; i<evaluations.length; i++){
				Evaluation eval = evaluations[i];
				appendString = " Evaluation: "+eval.getFoldId();
				appendString = appendString+" Microaverage Precision: "+eval.getMicroaveragePrecision()+" Microaverage Recall: "+eval.getMicroaverageRecall()+" Microaverage F1 "+eval.getMicroaverageF1();
				appendString = appendString+" Macroaverage Precision: "+eval.getMacroaveragePrecision()+" Macroaverage Recall: "+eval.getMacroaverageRecall()+" Macroaverage F1 "+eval.getMacroaverageF1(); 
				Category[] categories = refHub.getCategoryManager().getCategoryArray();
				for(int j=0; j<categories.length; j++){
					appendString = appendString + " Category: "+categories[j].getId()+
							" TP: "+eval.getTP(categories[j].getId())+" FP: "+eval.getFP(categories[j].getId())+
							" FN: "+eval.getFN(categories[j].getId())+
							" precision: "+eval.getPrecision(categories[j].getId())+" recall "+eval.getRecall(categories[j].getId())+
							" F1: "+eval.getF1(categories[j].getId());
				}
				model.appendToTrainingLog(appendString);
				if(selectionPolicy==SelectionPolicy.MicroaverageF1){
					if(eval.getMicroaverageF1()>maxValue){
						maxValue=eval.getMicroaverageF1();
						maxId=i;
					}
				}else if(selectionPolicy==SelectionPolicy.MicroaveragePrecision){
					if(eval.getMicroaveragePrecision()>maxValue){
						maxValue=eval.getMicroaveragePrecision();
						maxId=i;
					}
				}else if(selectionPolicy==SelectionPolicy.MicroaverageRecall){
					if(eval.getMicroaverageRecall()>maxValue){
						maxValue=eval.getMicroaverageRecall();
						maxId=i;
					}
				}else if(selectionPolicy==SelectionPolicy.MacroaverageF1){
					if(eval.getMacroaverageF1()>maxValue){
						maxValue=eval.getMacroaverageF1();
						maxId=i;
					}
				}else if(selectionPolicy==SelectionPolicy.MacroaveragePrecision){
					if(eval.getMacroaveragePrecision()>maxValue){
						maxValue=eval.getMacroaveragePrecision();
						maxId=i;
					}
				}else if(selectionPolicy==SelectionPolicy.MacroaverageRecall){
					if(eval.getMacroaverageRecall()>maxValue){
						maxValue=eval.getMacroaverageRecall();
						maxId=i;
					}
				}
			}
			model.appendToTrainingLog(" Best evaluation following the "+selectionPolicy.toString()+" Policy is: "+evaluations[maxId].getFoldId());
			refHub.getModelManager().setTrainingInProgress(false);
		}
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public C3CommitteeConfiguration getC3config() {
		return c3config;
	}

	public void setC3config(C3CommitteeConfiguration c3config) {
		this.c3config = c3config;
	}

	public long getModelId() {
		return modelId;
	}

	public void setModelId(long modelId) {
		this.modelId = modelId;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	
	
	
	
}
