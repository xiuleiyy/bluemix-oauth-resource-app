package com.bluemix.bankacct.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.ws.rs.GET;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.bluemix.bankacct.lib.ConfigParms;
import com.ibm.ws.cloudoe.security.client.ResourceProviderBuilder;
import com.ibm.ws.cloudoe.security.client.api.TokenValidator;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@SuppressWarnings("deprecation")
@Path("/")
public class CustomerResource {

	private static CustomerService serviceMongo = null;

	// Get customers information

	@SuppressWarnings("deprecation")
	@Path("/Customers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccount(@QueryParam("customerName") String customerName,
			@QueryParam("access_token") String access_token) throws Exception, ServletException {

		CustomerAcct tempCustomer = null;

		customerName = customerName.toUpperCase();

		System.out.println("CustomerResource: @QueryParam(customerName) is "
				+ customerName);

		System.out.println("CustomerResource: @QueryParam(access_token) is "
				+ access_token);
		JSONObject object = new JSONObject();
		String scopesStr = null;
		String username = null;
		String correctUsername = ConfigParms.USERNAME;
		ArrayList<?> scopesList = null;


			@SuppressWarnings("deprecation")
			DefaultHttpClient httpClient = Utils.createHttpClient();

			HttpResponse accessTokenResp = null;

			try {

				String CHECK_TOKEN_PARM = "token=" + access_token;

				HttpPost accessTokenReq = new HttpPost(
						ConfigParms.OAUTH20_CHECK_TOKEN_URI);
				HttpEntity reqEntity = new ByteArrayEntity(
						CHECK_TOKEN_PARM.getBytes(ConfigParms.DEFAULT_ENCODE));

				accessTokenReq
						.setHeader(ConfigParms.DEFAULT_CONTENT_TYPE_HEADER);
				accessTokenReq.setHeader(ConfigParms.DEFAULT_ACCEPT_HEADER);

				Credentials clientCredentials = new UsernamePasswordCredentials(
						ConfigParms.RESOURCE_ID, ConfigParms.RESOURCE_SECRET);
				accessTokenReq.addHeader(BasicScheme.authenticate(
						clientCredentials, ConfigParms.DEFAULT_ENCODE, false));

				accessTokenReq.setEntity(reqEntity);
				System.out.println("accessTokenReq.toString() is "
						+ accessTokenReq.toString());

				accessTokenResp = httpClient.execute(accessTokenReq);
				System.out.println("accessTokenResp is " + accessTokenResp);

				System.out.println("accessTokenResp.getStatusLine() is "
						+ accessTokenResp.getStatusLine());

				if (accessTokenResp.getStatusLine().toString().contains("200")) {
					
					String jsonStr = Utils.getContentFromResponse(accessTokenResp);

					@SuppressWarnings("unchecked")
					Map<String, ArrayList> respMap = Utils.getObjectFromJSONResponse(jsonStr, Map.class);
					scopesList = respMap.get(Utils.KEY_SCOPES);
					scopesStr = scopesList.toString();
					
					@SuppressWarnings("unchecked")
			        Map<String, String> respMap2 = Utils.getObjectFromJSONResponse(jsonStr, Map.class);
			        username = respMap2.get(Utils.KEY_USERNAME);

					System.out.println("Resource.CustomerResource scopesStr is: " + scopesStr);
					System.out.println("Resource.CustomerResource username is: " + username);
					
				} else {
					System.out.println("Your access_token is invalid");

					return null;
				}

			} catch (ClientProtocolException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			} finally {
				httpClient.getConnectionManager().shutdown();
			}

		

	
		if (correctUsername.equals(username)&&scopesStr.contains(ConfigParms.SCOPE)) {
			System.out.println("You access_token is valid");


			System.out.println("getAccount : Incoming request");

			serviceMongo = new CustomerServiceMongoImpl();
			tempCustomer = serviceMongo.getCustomer(customerName);

			if (tempCustomer == null) {

				System.out.println("getCustomer : No customer info retrieved");
				return Response.ok().build();
			}

			try {
				object.put("tempCustomer", tempCustomer);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return Response.ok().entity(object.toString()).build();

		} else {

			System.out.println("Your access_token is invalid");

			return null;

		}

	}

}
