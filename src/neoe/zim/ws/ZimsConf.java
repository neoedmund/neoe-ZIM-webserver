package neoe.zim.ws;

import java.io.FileInputStream;
import java.util.Map;

import neoe.util.Config;
import neoe.util.FileUtil;
import neoe.util.PyData;

public class ZimsConf {

	private static Map m;

	public static Object get(String name) throws Exception {
		if (m == null)
			m = (Map) PyData.parseAll(FileUtil.readString(new FileInputStream("zims.conf"), null));
		return Config.get(m, name);
	}

}
