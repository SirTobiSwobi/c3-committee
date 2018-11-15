package org.SirTobiSwobi.c3.c3committee.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.SirTobiSwobi.c3.c3committee.api.TCConfiguration;
import org.SirTobiSwobi.c3.c3committee.api.TCHash;
import org.SirTobiSwobi.c3.c3committee.api.TCModel;
import org.SirTobiSwobi.c3.c3committee.api.TCModels;
import org.SirTobiSwobi.c3.c3committee.api.TCProgress;
import org.SirTobiSwobi.c3.c3committee.core.Trainer;
import org.SirTobiSwobi.c3.c3committee.db.EvaluationManager;
import org.SirTobiSwobi.c3.c3committee.db.Model;
import org.SirTobiSwobi.c3.c3committee.db.ModelManager;
import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;

@Path("/models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelsResource {
	private ReferenceHub refHub;
	private Trainer trainer;

	public ModelsResource(ReferenceHub refHub, Trainer trainer) {
		super();
		this.refHub = refHub;
		this.trainer = trainer;
	}
	
	@GET
	public Response getModels(@QueryParam("hash") String hash){
		if(hash!=null&&hash.equals("1")){
			TCHash h = new TCHash("models",refHub.getModelManager().getModelHash());
			return Response.ok(h).build();
		}else{
			Model[] models = refHub.getModelManager().getModelArray();
			TCModel[] TCmodelArray = new TCModel[models.length];
			for(int i=0; i<models.length;i++){
				Model mod = models[i];
				TCConfiguration config = ConfigurationResource.buildTCConfiguration(mod.getConfiguration());
				TCModel TCmod = new TCModel(mod.getId(),mod.getConfiguration().getId(), mod.getProgress(), mod.getTrainingLog(), config);
				TCmodelArray[i]=TCmod;
			}
			TCModels TCmodels;
			if(models.length>0){
				TCmodels = new TCModels(TCmodelArray);
			}else{
				TCmodels = new TCModels();
			}
			return Response.ok(TCmodels).build();
				
				
		}	
	}
	
	@POST
	public Response startTraining(@QueryParam("confId") long conf){
		
		if(!refHub.getConfigurationManager().containsConfiguration(conf)){
			Response response = Response.status(400).build();
			return response;
		}
		else{
			//Spawn training progress
			if(!refHub.getModelManager().isTrainingInProgress()){
				long modId=refHub.getModelManager().addModelWithoutId(conf);
				TCProgress progress = new TCProgress("/models/"+modId,.0);
				trainer.startTraining(conf, modId);
				Response response = Response.ok(progress).build();
				return response;
			}else{
				long modId=refHub.getModelManager().getMaxId();//computes currently used modelId;
				TCProgress progress = new TCProgress("/models/"+modId,refHub.getModelManager().getModelByAddress(modId).getProgress());
				Response response = Response.ok(progress).build();
				return response;
			}
			
			
		}		
	}
	
	@DELETE
	public Response deleteAllModels(){
		refHub.setModelManager(new ModelManager());
		refHub.getModelManager().setRefHub(refHub);
		refHub.setEvaluationManager(new EvaluationManager());
		refHub.getEvaluationManager().setRefHub(refHub);
		Response response = Response.ok().build();
		return response;
	}

}
