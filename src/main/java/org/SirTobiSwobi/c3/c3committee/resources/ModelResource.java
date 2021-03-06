package org.SirTobiSwobi.c3.c3committee.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.SirTobiSwobi.c3.c3committee.api.TCAthlete;
import org.SirTobiSwobi.c3.c3committee.api.TCConfiguration;
import org.SirTobiSwobi.c3.c3committee.api.TCModel;
import org.SirTobiSwobi.c3.c3committee.api.TCProgress;
import org.SirTobiSwobi.c3.c3committee.db.Configuration;
import org.SirTobiSwobi.c3.c3committee.db.Model;
import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;
import org.SirTobiSwobi.c3.c3committee.db.SelectionPolicy;

@Path("/models/{mod}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelResource {
	private ReferenceHub refHub;
	
	public ModelResource(ReferenceHub refHub) {
		super();
		this.refHub = refHub;
	}

	@GET
	public Response getModel(@PathParam("mod") long mod){
		if(!refHub.getModelManager().containsModel(mod)){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		Model model = refHub.getModelManager().getModelByAddress(mod);
		
		if(model.getProgress()<1.0){
			TCProgress output = new TCProgress("/models/"+mod,model.getProgress());
			return Response.ok(output).build();
		}else{
			TCModel output = buildTCModel(model, refHub);
			return Response.ok(output).build();
		}
		
		
		
	}
	
	@DELETE
	public Response deleteModel(@PathParam("mod") long mod){
		if(!refHub.getModelManager().containsModel(mod)){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		refHub.getModelManager().deleteModel(mod);
		refHub.getEvaluationManager().deleteTrainingSession(mod);
		Response response = Response.ok().build();
		return response;
	}
	
	public static TCModel buildTCModel(Model model, ReferenceHub refHub){
		if(model==null){
			return null;
		}
		
		Configuration conf = model.getConfiguration();
		String selectionPolicy="MicroaverageF1";
		if(conf.getSelectionPolicy()==SelectionPolicy.MacroaverageF1){
			selectionPolicy="MicroaverageF1";
		}else if(conf.getSelectionPolicy()==SelectionPolicy.MicroaveragePrecision){
			selectionPolicy="MicroaveragePrecision";
		}else if(conf.getSelectionPolicy()==SelectionPolicy.MicroaverageRecall){
			selectionPolicy="MicroaverageRecall";
		}else if(conf.getSelectionPolicy()==SelectionPolicy.MacroaverageF1){
			selectionPolicy="MacroaverageF1";
		}else if(conf.getSelectionPolicy()==SelectionPolicy.MacroaveragePrecision){
			selectionPolicy="MacroaveragePrecision";
		}else if(conf.getSelectionPolicy()==SelectionPolicy.MacroaverageRecall){
			selectionPolicy="MacroaverageRecall";
		}
		TCAthlete[] athletes = new TCAthlete[conf.getAthletes().length];
		for(int i=0;i<athletes.length;i++){
			athletes[i]=new TCAthlete(conf.getAthletes()[i].getId(),conf.getAthletes()[i].getUrl(),conf.getAthletes()[i].getDescription());
		}
		TCConfiguration configuration = new TCConfiguration(conf.getId(), conf.getFolds(), conf.isIncludeImplicits(), conf.getAssignmentThreshold(),
				selectionPolicy, athletes);
		TCModel output = new TCModel(model.getId(), model.getConfiguration().getId(), model.getProgress(), model.getTrainingLog(), configuration, model.getWeights(),model.getTrainingSetSize());
		return output;
	}
}
