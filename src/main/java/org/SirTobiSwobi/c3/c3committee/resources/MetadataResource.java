package org.SirTobiSwobi.c3.c3committee.resources;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.SirTobiSwobi.c3.c3committee.C3CommitteeConfiguration;
import org.SirTobiSwobi.c3.c3committee.api.TCMetadata;

@Path("/metadata")
@Produces(MediaType.APPLICATION_JSON)
public class MetadataResource {
	private C3CommitteeConfiguration configuration;
	
	public MetadataResource(){
		super();
	}
	
	public MetadataResource(C3CommitteeConfiguration configuration){
		this.configuration = configuration;
	}
	
	@GET
    @Timed
	public TCMetadata getMetadata(){
		TCMetadata metadata = new TCMetadata(
				configuration.getName(),
				configuration.getCalls(),
				configuration.getAlgorithm(),
				configuration.getPhases(),
				configuration.getAlgorithm(),
				configuration.getConfiguration(),
				configuration.getConfigOptions(),
				configuration.getArchetype(),
				configuration.getRunType(),
				configuration.getDebugExamples()
				);
		
		return metadata;
		
	}
	
}
