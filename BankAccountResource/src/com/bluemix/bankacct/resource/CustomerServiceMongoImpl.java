package com.bluemix.bankacct.resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;



public class CustomerServiceMongoImpl implements CustomerService{
	
    public CustomerAcct getCustomer(String customerName) throws Exception {
    
    	CustomerAcct tempCustomer = new CustomerAcct();
    	
    	System.out.println("receiveing customerName is " + customerName);
    		
		String hostname = EnvironmentUtils.getHostName();
		String port = EnvironmentUtils.getPort();
		String dbName = EnvironmentUtils.getDatabaseName();
		String uid = EnvironmentUtils.getUid();
		String pwd = EnvironmentUtils.getPwd();

		Mongo mongo = new Mongo(hostname, Integer.parseInt(port));
		DB db = mongo.getDB(dbName);

		// Authentication
		boolean flag = db.authenticate(uid, pwd.toCharArray());
		System.out.println("flag:::::test " + flag);

		DBCollection accounts = db.getCollection("accounts");

		BasicDBObject findName = new BasicDBObject();
		findName.put("name", customerName);
		DBCursor cursor = accounts.find(findName);
		

		if (cursor.hasNext()) {
			DBObject match = cursor.next();
			if (match != null) {
				
				tempCustomer.setCustomerAcct(Integer.valueOf(match.get("id")
						.toString()));
				tempCustomer.setCustomerName(match.get("name").toString());
				tempCustomer.setCustomerMoney(Double.valueOf(match.get("money")
						.toString()));
				System.out.println("CustomerName is: " + customerName + " Customer Money is: " + tempCustomer.getCustomerMoney());
				
			} else {
				System.out.println("Not found in database: " + customerName + "");
						
			}
		}
		
		else{
			System.out.println("Not found in database: " + customerName + "");
		}
		 return tempCustomer;
    
    }
	

}
