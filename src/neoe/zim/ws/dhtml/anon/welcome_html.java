package neoe.zim.ws.dhtml.anon;

import java.util.Map;

import neoe.httpd.IHttpRequest;
import neoe.httpd.IHttpResponse;
import neoe.httpd.ITemplateData;
import neoe.zim.ws.ZimsConf;

public class welcome_html implements ITemplateData {

	@Override
	public void serve(IHttpRequest req, IHttpResponse resp, Map m) throws Exception {
		m.put("c", getContent(m));
	}

	private String getContent(Map m) throws Exception {
		StringBuilder sb = new StringBuilder();
		Map zims = (Map) ZimsConf.get("zims");
		sb.append("Hosted zims:<br/>");
		for (Object key : zims.keySet()) {
			String zn = (String) key;
			sb.append(String.format(
					"<br/><i>%s</i><br/><input type=text class=search_input id='k_%s'><br/>&nbsp;&nbsp;<a href='javascript:search1(\"%s\")'>search</a><br/>"
							+ "&nbsp;&nbsp;<a href='javascript:openUrl1(\"%s/%s/random\")'>random</a><br/>",
					zn, zn, zn, m.get("baseurl"),zn));
		}
		return sb.toString();
	}
}
