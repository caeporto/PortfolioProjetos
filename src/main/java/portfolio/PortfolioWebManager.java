package portfolio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import portfolio.models.User;
import portfolio.models.Project;
import portfolio.util.Util;

/**
 * Hello world!
 *
 */
public class PortfolioWebManager 
{			
    public static void main( String[] args ) throws InterruptedException, ClientProtocolException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException
    {
    	boolean loggedIn = false;    	
    	RequestConfig globalConfig = RequestConfig.custom()
    			.setCookieSpec(CookieSpecs.STANDARD).build();
    	BasicCookieStore cookieStore = PortfolioWebManager.retrieveCookies();
    	if(cookieStore == null)
    		cookieStore = new BasicCookieStore();
    	else
    		loggedIn = PortfolioWebManager.checkLoggedIn(cookieStore);
    	HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    	CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
    			.setSSLContext(builder.build())
    			.setDefaultRequestConfig(globalConfig)
                .setDefaultCookieStore(cookieStore)
                .build();
    	httpclient.start();
    	if(!loggedIn)
    	{
    		System.out.println("Voce nao esta logado");
    		PortfolioWebManager.login(httpclient, context);
    	}
    	else
    	{
    		System.out.println("Voce esta logado");
    		//retrieving user
    		User.getInstance().getPreferences();
    		if(User.getInstance().usertype == User.UserType.PortfolioManager.getValue())
    		{
    			Scanner sc = new Scanner(System.in);
    			Integer read = -1;
    			do {
		    		System.out.println("(1) Meus Projetos");
		    		System.out.println("(2) Meus Programas");
		    		System.out.println("(3) Cadastrar Projeto");
		    		System.out.println("(4) Criar Programa");
		    		System.out.println("(5) Sair");
	
		    		read = sc.nextInt();
		    		switch(read)
		    		{
		    		case 1:
		    			if(User.getInstance().projects.size() == 0)
		    			{
			    			List<String> projects = User.getInstance().ref_projects;
			    	        final CountDownLatch latch = new CountDownLatch(projects.size());
			    	        for(String id : projects)
			    	        {
			    	        	PortfolioWebManager.getProject(httpclient, context, latch, id);
			    	        	latch.await();
			    	        }
			    	        for(Project p : User.getInstance().projects)
			    	        {
			    	        	System.out.println(p.toString());
			    	        }
		    			}
		    			else
		    			{
		    				for(Project p : User.getInstance().projects)
			    	        {
			    	        	System.out.println(p.toString());
			    	        }
		    			}
		    		}
		    		
    			}while(read != 5);
	    		
	    		sc.close();
    		}

    		//PortfolioWebManager.getPreferences(User.getInstance()); //not async
    		//System.out.println(User.getInstance().toString());
    		//PortfolioWebManager.upload(httpclient, context);
    		//System.out.println("Está logado");
    		//PortfolioWebManager.login(httpclient, context);
    	}
    }
    
    public static JSONObject getProject(CloseableHttpAsyncClient httpClient, HttpClientContext context, CountDownLatch latch, String projectId)
    {
    	HttpGet getMethod = new HttpGet(Util.LOGIN_MYPROJECT+projectId);

    	httpClient.execute(getMethod, context, new FutureCallback<HttpResponse>(){

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				 latch.countDown();
			}

			@Override
			public void completed(HttpResponse response) {
				// TODO Auto-generated method stub
				latch.countDown();
				int code = response.getStatusLine().getStatusCode();
                if(code == HttpStatus.SC_OK)
                {
	                HttpEntity entity = response.getEntity();
	                try {
						String responseString = EntityUtils.toString(entity, "UTF-8");
						JSONObject json = new JSONObject(responseString);
						Project project = new Project();
						try {
							project.readJSON(json);
							User.getInstance().projects.add(project);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (java.text.ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (ParseException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
			}

			@Override
			public void failed(Exception arg0) {
				// TODO Auto-generated method stub
				 latch.countDown();
			}
    		
    	});
    	
    	return null;
    }
    
    public static boolean checkLoggedIn(BasicCookieStore store){
    	Date now = new Date();
    	for(Cookie cookie : store.getCookies())
    	{
    		//System.out.println("Cookie: "+cookie.getName());
    		if(cookie.getName().equals("connect.sid"))
    		{
    			if(cookie.isExpired(now))
    			{
    				store.clearExpired(now);
    				return false;
    			}
    			else
    				return true;
    		}
    	}
    	return false;
    }
    
    public static void login(CloseableHttpAsyncClient httpClient, HttpClientContext context) throws InterruptedException
    {
    	JSONObject json = new JSONObject("{ email : caetano.silva@acad.pucrs.br, password : blablabla }");
        StringEntity requestEntity = new StringEntity(
        	    json.toString(),
        	    ContentType.APPLICATION_JSON);
                
        HttpPost postMethod = new HttpPost(Util.LOGIN);
        postMethod.setEntity(requestEntity);
        
        final CountDownLatch latch = new CountDownLatch(1);
        httpClient.execute(postMethod, context, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response) {
                latch.countDown();
                int code = response.getStatusLine().getStatusCode();
                if(code == HttpStatus.SC_OK)
                {
	                HttpEntity entity = response.getEntity();
	                try {
						String responseString = EntityUtils.toString(entity, "UTF-8");
						JSONObject json = new JSONObject(responseString);
						System.out.println(json.toString());
						User.getInstance().readJSON(json);
						System.out.println(User.getInstance().toString());
				    	//save preferences
						User.getInstance().savePreferences();
				    	PortfolioWebManager.saveCookies((BasicCookieStore) context.getCookieStore());
					} catch (ParseException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }

            public void failed(final Exception ex) {
                latch.countDown();
            }

            public void cancelled() {
                latch.countDown();
                System.out.println(postMethod.getRequestLine() + " cancelled");
            }

        });
        latch.await();
    }
    
    public static byte[] serializeObject(Object obj)
    {
    	final ByteArrayOutputStream outbuffer = new ByteArrayOutputStream();
        ObjectOutputStream outstream;
		try {
			outstream = new ObjectOutputStream(outbuffer);
			outstream.writeObject(obj);
	        outstream.close();
	        return outbuffer.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public static Object deserializeObject(byte[] raw)
    {
    	final ByteArrayInputStream inbuffer = new ByteArrayInputStream(raw);
        ObjectInputStream instream;
		try {
			instream = new ObjectInputStream(inbuffer);
			Object obj = instream.readObject();
			return obj;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public static void saveCookies(BasicCookieStore store){
		try {
			FileOutputStream fileOut = new FileOutputStream(Util.COOKIE_FILE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(store);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static BasicCookieStore retrieveCookies(){
		try {
			 FileInputStream fileIn = new FileInputStream(Util.COOKIE_FILE);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         BasicCookieStore store = (BasicCookieStore) in.readObject();
	         in.close();
	         fileIn.close();
	         return store;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
    }
        
    public static void upload(CloseableHttpAsyncClient httpClient, HttpClientContext context) throws ClientProtocolException, IOException, InterruptedException
    {
        HttpPost httppost = new HttpPost(Util.LOGIN_UPLOADFILE+"57f845f0f7f6f3d1c4393820");

        FileBody bin = new FileBody(new File("test.txt"));

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("userfile", bin)
                .build();

        httppost.setEntity(reqEntity);
        
        final CountDownLatch latch = new CountDownLatch(1);
        httpClient.execute(httppost, context, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response) {
                latch.countDown();
                HttpEntity entity = response.getEntity();
                try {
					String responseString = EntityUtils.toString(entity, "UTF-8");
					System.out.println(responseString);
//					JSONObject json = new JSONObject(responseString);
//					System.out.println(json.toString());
//					User.getInstance().readJSON(json);
//					System.out.println(User.getInstance().toString());
//			    	//save cookie after request updating time
			    	PortfolioWebManager.saveCookies((BasicCookieStore) context.getCookieStore());
				} catch (ParseException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println(httppost.getRequestLine() + "->" + response.getStatusLine());
            }

            public void failed(final Exception ex) {
                latch.countDown();
                System.out.println(httppost.getRequestLine() + "->" + ex);
            }

            public void cancelled() {
                latch.countDown();
                System.out.println(httppost.getRequestLine() + " cancelled");
            }

        });
        latch.await();
    }
}
