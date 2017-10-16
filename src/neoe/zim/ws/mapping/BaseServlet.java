package neoe.zim.ws.mapping;

import java.util.Map;
import java.util.Random;

import neoe.httpd.HttpConsts;
import neoe.httpd.HttpdU;
import neoe.httpd.IHttpRequest;
import neoe.httpd.IHttpResponse;
import neoe.httpd.IServlet;
import neoe.httpd.mapping.ServiceServ;
import neoe.httpd.mapping.TemplateServ;
import neoe.httpd.mapping.U;
import neoe.util.Log;
import neoe.zim.ws.ZimServ;
import neoe.zim.ws.ZimsConf;

public class BaseServlet implements IServlet {

	@Override
	public void serve(IHttpRequest req, IHttpResponse resp) throws Exception {

		String path = req.path();
		Log.log(String.format("[Base]%s", path));
		if (HttpdU.confirmToHttps(req, resp)) {
			return;
		}
		path = U.normal(path);

		if (path.contains("..") || U.containsWebInfo(path)) {
			// security
			resp.status(HttpConsts.HTTP_NOTFOUND);
			resp.flushHeader();
			return;
		}

		if (path.startsWith(U.C("SERVICE_PREFIX"))) {
			new ServiceServ().serve(req, resp);
			return;
		} else if (path.startsWith(U.C("TEMPLATE_PREFIX"))) {
			new TemplateServ().serve(req, resp);
			return;
		}
		String zimName;
		{
			if (path.startsWith("/"))
				path = path.substring(1);
			if (path.isEmpty()) {
				resp.sendRedirectNoData(HttpdU.addPath(new String[] { req.path(), "/dhtml/anon/welcome.html" }));
				// resp.writeContent("zim:" + ZimsConf.get("zims"));
				return;
			}
			int p1 = path.indexOf('/');
			if (p1 < 0) {
				zimName = path;
				path = "";
			} else {
				zimName = path.substring(0, p1);
				path = path.substring(p1 + 1);
			}
		}
		Map zimmap = (Map) ZimsConf.get("zims");
		String zimfn = (String) zimmap.get(zimName);
		if (zimfn != null) {
			final int retry = 15;
			for (int i = 0; i < retry; i++) {
				try {
					new ZimServ().serve(req, resp, zimfn, path);
					return;
				} catch (OutOfMemoryError e) {
					Thread.sleep(100 + rand.nextInt(300));
					System.out.println("OOM but retry:" + i);
					continue;
				} catch (Throwable e) {
					e.printStackTrace();
					resp.writeContent(e.toString());
					return;
				}
			}
			resp.status(HttpConsts.HTTP_INTERNALERROR);
			resp.writeContent("server too busy:" + path);
			return;
		} else {
			// new StaticServ().serve(req, resp);
			resp.status(HttpConsts.HTTP_NOTFOUND);
			resp.writeContent("not found:" + path);
			return;
		}
	}

	static Random rand = new Random();

}
