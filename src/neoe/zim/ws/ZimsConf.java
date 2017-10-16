package neoe.zim.ws;

import java.util.Map;

import neoe.util.Config;

public class ZimsConf {

	private static Map m;

	public static Object get(String name) throws Exception {
		return Config.getConfig("zims.conf").get(name);
	}

}
