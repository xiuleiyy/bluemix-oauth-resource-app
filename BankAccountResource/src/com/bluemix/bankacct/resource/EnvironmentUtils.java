package com.bluemix.bankacct.resource;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;


public class EnvironmentUtils {
	public static String host = null;
	public static String databaseName = null;
	public static String port = null;
	public static String uid = null;
	public static String pwd = null;
	public static String uri = null;
	public static String uri_value = null;
	public static String uid_pwd = null;
	public static String host_port_dbname = null;
	public static String host_port = null;
 
	JSONArray mongo_services = null;
	CustomerAcct accessCountObject = null;
	CustomerAcct accessCountSession = null;

	public static void getMongoInfo() {

		JSONObject vcap_service_obj = null;
		String vcap_service_str = System.getenv("VCAP_SERVICES"); 
		System.out.println("vcap_service_str" + vcap_service_str);
				
		if (null != vcap_service_str && vcap_service_str.length() > 0) {
			System.out.println("getMongoInfo : found icap_services env var");
			try {
				vcap_service_obj = JSONObject.parse(vcap_service_str);
				System.out.println("vcap_service_obj" + vcap_service_obj);
				System.out.println("getting vcap_service_obj details, size:"
						+ vcap_service_obj.size());
				System.out.println("getting vcap_service_obj keyset:"
						+ vcap_service_obj.keySet());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		JSONArray mongo_services = (JSONArray) vcap_service_obj.get("mongodb-2.2");
//		JSONObject first_myMongo = (JSONObject) mongo_services.get(0);
//		JSONObject first_credential = (JSONObject) first_myMongo
//				.get("credentials");
//		host = (String) first_credential.get("host");
//		System.out.println("host: " + host);
//		port = first_credential.get("port").toString();
//		databaseName = (String) first_credential.get("db");
//		uid = (String) first_credential.get("username");
//		pwd = (String) first_credential.get("password");
		
		JSONArray mongo_services = (JSONArray) vcap_service_obj.get("mongolab");
		JSONObject first_myMongo = (JSONObject) mongo_services.get(0);
		JSONObject first_credential = (JSONObject) first_myMongo
				.get("credentials");
		uri =(String) first_credential.get("uri");
		
		uri_value = uri.substring(10);
		System.out.println("uri_value = " + uri_value);
		
		uid_pwd= uri_value.substring(0,uri_value.indexOf("@"));
		host_port_dbname = uri_value.substring(uri_value.indexOf("@") + 1);
		
		uid=uid_pwd.substring(0, uid_pwd.indexOf(":"));
		pwd=uid_pwd.substring(uid_pwd.indexOf(":")+1);
		System.out.println("uid = " + uid);
		System.out.println("pwd = " + pwd);	
		
		host_port= host_port_dbname.substring(0, host_port_dbname.indexOf("/"));
		databaseName = host_port_dbname.substring(host_port_dbname.indexOf("/")+1);
		System.out.println("databaseName = " + databaseName);
		
		host = host_port.substring(0, host_port.indexOf(":"));
		port = host_port.substring(host_port.indexOf(":")+1);
		System.out.println("host = " + host);
		System.out.println("port = " + port);

	}

	public static String getHostName() {
		if(host == null) {
			getMongoInfo();
		}
		return host;
	}

	public static String getDatabaseName() {
		if(databaseName == null) {
			getMongoInfo();
		}
		return databaseName;
	}

	public static String getPort() {
		if(port == null) {
			getMongoInfo();
		}
		return port;
	}
	
	public static String getUid() {
		if(uid == null) {
			getMongoInfo();
		}
		return uid;
	}
	
	public static String getPwd() {
		if(pwd == null) {
			getMongoInfo();
		}
		return pwd;
	}

	public static boolean databaseExists(Mongo mongo) {
		boolean exists = false;

		// get the names of existing databases and check to make sure we do
		// not make a duplicate
		DB db = mongo.getDB("test");
		Set<String> collectionNames = db.getCollectionNames();
		for (String collections : collectionNames) {
			if (collections.toLowerCase().equals("accounts")) {
				exists = true;
			}
		}

		return exists;
	}
	
	public static DBCollection getMongoCollection (String collectionName) {
		getMongoInfo();		

		Mongo mongo = null;
		try {
			mongo = new Mongo(host, Integer.parseInt(port)); 
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = mongo.getDB(databaseName);
		
		//Authentication
		boolean flag = db.authenticate(uid, pwd.toCharArray());
		System.out.println("flag::::: " + flag);
		
		DBCollection mongoCollection = db.getCollection(collectionName);
		
		return mongoCollection;
	}

	
}