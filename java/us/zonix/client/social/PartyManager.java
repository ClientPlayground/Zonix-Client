package us.zonix.client.social;

import java.util.HashSet;
import java.util.Set;

public final class PartyManager {

	private final Set<String> partyMembers = new HashSet<>();

	public PartyManager() {
		this.partyMembers.add("Erouax");
		this.partyMembers.add("Manthe");
//		this.partyMembers.add("Hitler");
	}

	private boolean leader;

	public Set<String> getPartyMembers() {
		this.partyMembers.clear();
		this.partyMembers.add("Erouax");
		this.partyMembers.add("Manthe");
				this.partyMembers.add("Hitler");
		return partyMembers;
	}

	public boolean isLeader() {
		return leader;
	}

	public boolean isInParty() {
//		return !this.partyMembers.isEmpty();
		return true;
	}

}
