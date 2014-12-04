package edu.thu.keg.link.common.request;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class Request
{
	private static CloseableHttpClient httpClient = null;

	static {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
	}
	
	private Request()
	{}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}
	public static String post(String url,List<String[]> args) {
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		for(String[] pairs:args)
		{
			 nvps.add(new BasicNameValuePair(pairs[0], pairs[1]));

		}

		InputStreamReader isr = null;
		StringBuilder sb=new StringBuilder();

		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = httpClient.execute(post);
			// 先从响应头得到实体
			HttpEntity entity = response.getEntity();
			// 得到实体输入流

			
			isr =new InputStreamReader(entity.getContent(),"UTF-8");

			char[] buf=new char[1024];
			
			int len=0;
			while (true) {
				
				len=isr.read(buf);
				if (len <0) {
					break;
				}
				sb.append(buf,0,len);
			
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					isr.close();

				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	public static String get(String path) {
		
		HttpGet get = new HttpGet(path);
		
		//get.addHeader("User-agent", "Mozilla/5.0");
		// List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// nvps.add(new BasicNameValuePair("titles",
		// "Java_(programming_language)"));
		// nvps.add(new BasicNameValuePair("prop", "revisions"));
		// nvps.add(new BasicNameValuePair("rvprop", "content"));
		// nvps.add(new BasicNameValuePair("format", "xml"));

		// action=query&prop=revisions&titles=Java_(programming_language)&rvprop=content&format=xml

		
		StringBuilder sb=new StringBuilder();
		
		InputStreamReader isr = null;
		try {
			// post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = httpClient.execute(get);
			// 先从响应头得到实体
			HttpEntity entity = response.getEntity();
			// 得到实体输入流

			isr =new InputStreamReader(entity.getContent(),"UTF-8");

			char[] buf=new char[1024];
			
			int len=0;
			while (true) {
				
				len=isr.read(buf);
				if (len <0) {
					break;
				}
				sb.append(buf,0,len);
			
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					isr.close();

				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

}
