package us.zonix.client.util;

public class ProxyData {

    // cucked naming conventions because these names are used in the JSON response
    private int sa_proxy;
    private int us_proxy;
    private int eu_proxy;
    private int as_proxy;

    public int getPlayerCount(int index) {
        switch (index) {
            case 0: return us_proxy;
            case 1: return eu_proxy;
            case 2: return sa_proxy;
            case 3: return as_proxy;
            default: return -1;
        }
    }

}
