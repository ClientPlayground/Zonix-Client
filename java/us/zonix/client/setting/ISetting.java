package us.zonix.client.setting;

public interface ISetting<T> {

	String getName();

	T getValue();

	void setValue(T value);

}
