package org.SirTobiSwobi.c3.c3committee.core;

import javax.ws.rs.client.Client;

import org.SirTobiSwobi.c3.c3committee.db.ReferenceHub;

public class Classifier {
	private ReferenceHub refHub;
	private Client client;
	
	public Classifier(ReferenceHub refHub) {
		super();
		this.refHub = refHub;
	}
	
	public boolean categorizeDocument(long docId){
		//spawn classification thread;
		boolean categorizationPossible=false;
		if(refHub.getDocumentManager().containsDocument(docId)&&refHub.getCategoryManager().getSize()>0){
			categorizationPossible=true;
		}
		if(categorizationPossible){
			new CommitteeThread(refHub,docId,client).run();
		}
		return categorizationPossible;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	
}
