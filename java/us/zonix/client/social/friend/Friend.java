package us.zonix.client.social.friend;

import java.util.UUID;

public final class Friend {

	private final UUID uniqueId;
	private final String name;
	private boolean online;

	public Friend(UUID uniqueId, String name) {
		this.uniqueId = uniqueId;
		this.name = name;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public String getName() {
		return name;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
