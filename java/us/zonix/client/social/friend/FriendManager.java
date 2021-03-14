package us.zonix.client.social.friend;

import java.util.HashSet;
import java.util.Set;

public final class FriendManager {

	private final Set<Friend> friends = new HashSet<>();

	public FriendManager() {

	}

	public Set<Friend> getFriends() {
		return friends;
	}

	public Set<Friend> getOnlineFriends() {
		Set<Friend> friends = new HashSet<>();
		for (Friend friend : this.friends) {
			if (friend.isOnline()) {
				friends.add(friend);
			}
		}
		return friends;
	}

}
