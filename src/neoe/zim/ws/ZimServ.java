package neoe.zim.ws;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Random;

import neoe.httpd.FileServ;
import neoe.httpd.HttpConsts;
import neoe.httpd.HttpdU;
import neoe.httpd.IHttpRequest;
import neoe.httpd.IHttpResponse;
import neoe.zim.Entry;
import neoe.zim.U;
import neoe.zim.Zim;

public class ZimServ {

	public void serve(IHttpRequest req, IHttpResponse resp, String zimfn, String path) throws Exception {

		if (path.startsWith("url/")) {

			String url = path.substring(4);
			url = URLDecoder.decode(url, "UTF8");
			Zim zim = new Zim(zimfn);
			int up = zim.findUrlPos(url);
			if (up < 0) {
				System.out.println("404:" + url);
				{// fix buggy source
					if (url.startsWith("A/I/") || url.startsWith("A/-/")) {
						url = url.substring(2);
						up = zim.findUrlPos(url);
						if (up < 0) {
							System.out.println("404b:" + url);
						} else {
							writeEntry(resp, zim, up);
							return;
						}
					}
				}
				resp.status(HttpConsts.HTTP_NOTFOUND);
				resp.writeContent("url not found:" + url);
			} else {
				writeEntry(resp, zim, up);
			}

		} else if (path.equals("random")) {
			// show
			Zim zim = new Zim(zimfn);
			int ui = rand.nextInt(zim.articleCount);
			System.out.println("show random:" + ui);
			Entry e = zim.getEntryByUrlIndex(ui);
			String path1 = req.path();
			path1 = path1.substring(0, path1.length() - path.length());
			String path2 = path1 + "url/" + U.toStr(e.url);
			resp.sendRedirectNoData(path2);
		}
		if (path.startsWith("topic/")) {
			String topic = path.substring("topic/".length());
			if (topic.length() > 0) {
				Zim zim = new Zim(zimfn);
				int tp = zim.findTitlePos(topic);
				if (tp > 0) {
					Entry e = zim.getEntryByTitleIndex(tp);
					String path1 = req.path();
					path1 = path1.substring(0, path1.length() - path.length());
					String path2 = path1 + "url/" + U.toStr(e.url);
					resp.sendRedirectNoData(path2);
				}
			}
		}

		{
			resp.status(HttpConsts.HTTP_NOTFOUND);
			resp.writeContent("Page not found:" + path);
		}

	}

	private void writeEntry(IHttpResponse resp, Zim zim, int ui) throws IOException {
		Entry e = zim.getEntryByUrlIndex(ui);
		if (e.type == 1) {
			e = zim.getEntryByUrlIndex(e.redirectIndex);
		}
		long ts = new File(zim.fn).lastModified();
		resp.setHeader("ETag", Long.toString(ts, 36));
		resp.setHeader("Last-Modified", HttpdU.gmt(ts));
		String mime = FileServ.getMime(U.toStr(e.url));
		if (mime != null) {
			resp.setHeader(HttpConsts.HEAD_CONTENT_TYPE, mime);
		}
		byte[] bs = zim.getContent(ui);
		resp.writeContent(bs);
	}

	static Random rand = new Random();

}
