package com.bluemix.bankacct.resource;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;


@WebServlet("/PopulateDB")
public class PopulateDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String[] names = {"Albert Einstein",
			"Groucho Marx", "Harpo Marx", "Chico Marx", "Zeppo Marx",
			"Eleanor Roosevelt", "Jean-Paul Sartre", "Ben Franklin" };
	private double[] money = { 100.00, 186282.00, 2000.00, 20000.00, 300.00, 400.00,
			101118.84, 500.00 };

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PopulateDB() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		CustomerAcct ca = null;

		// Read the mongo environment Variable
		String hostname = EnvironmentUtils.getHostName();
		String port = EnvironmentUtils.getPort();
		String dbName = EnvironmentUtils.getDatabaseName();
		String uid = EnvironmentUtils.getUid();
		String pwd = EnvironmentUtils.getPwd();

		Mongo mongo = new Mongo(hostname, Integer.parseInt(port));
		DB db = mongo.getDB(dbName);

		// Authentication
		boolean flag = db.authenticate(uid, pwd.toCharArray());
		System.out.println("flag::::: " + flag);

		DBCollection accounts = db.getCollection("accounts");
		accounts.drop();

		for (short j = 0; j < 8; j++) {
			ca = new CustomerAcct();
			ca.setCustomerAcct((short) (j + 1));
			ca.setCustomerMoney((double) (money[j]));
			ca.setCustomerName(names[j].toUpperCase());
			// Create a Mongo Database object with each customer's
			// attributes to insert into Mongo.
			BasicDBObject customer = new BasicDBObject("name",
					ca.getCustomerName()).append("id", ca.getCustomerAcct())
					.append("money", ca.getCustomerMoney());
			accounts.insert(customer);
		}

		printResults(out, response);

	}

	private void printResults(PrintWriter out, HttpServletResponse response) {
		
		response.setContentType("text/html");
		String title = "Database Population Results";

		out.println("<HTML><HEAD><link rel=\"stylesheet\" href=\"css/style.css\"><TITLE>");
		out.println(title);
		out.println("</TITLE></HEAD><body><div class = 'container'>");
		out.println("<H1 align=\"center\">" + title + "</H1>");
		out.println("<BR><BR><BR>");
		out.println("<div align = 'center'><h3><B>Database Population: Successful</B></h3></div>");

		out.println("</div>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
