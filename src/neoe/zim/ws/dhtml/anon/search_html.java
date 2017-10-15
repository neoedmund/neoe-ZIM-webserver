package neoe.zim.ws.dhtml.anon;

import java.util.List;
import java.util.Map;

import neoe.httpd.HttpConsts;
import neoe.httpd.IHttpRequest;
import neoe.httpd.IHttpResponse;
import neoe.httpd.ITemplateData;
import neoe.zim.Entry;
import neoe.zim.U;
import neoe.zim.Zim;
import neoe.zim.ws.ZimsConf;

public class search_html implements ITemplateData {

	@Override
	public void serve(IHttpRequest req, IHttpResponse resp, Map m) throws Exception {
		String zimname = req.form("zim");
		String k = req.form("k");
		Map zims = (Map) ZimsConf.get("zims");
		String fn = (String) zims.get(zimname);
		if (fn == null) {
			resp.status(HttpConsts.HTTP_NOTFOUND);
			return;
		}
		StringBuilder sb = new StringBuilder();
		Zim zim = new Zim(fn);
		int pos = zim.findTitlePos(k);
		List<String> res = zim.getTitlesNear(pos, 30);
		int cnt = 30;
		for (int i = pos; i < pos + cnt; i++) {
			if (i >= zim.articleCount || i < 0)
				break;
			Entry e = zim.getEntryByTitleIndex(i);
			sb.append(
					String.format("<a href='/zim/%s/url/%s'>%s</a> <br/>", zimname, U.toStr(e.url), U.toStr(e.title)));
		}
		if (res.isEmpty()) {
			sb.append("no result for `" + k + "`");
		}
		m.put("c", sb.toString());
		
	}

}
