package com.bluemix.bankacct.resource;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * Servlet implementation class CustomerCredit
 */
@WebServlet("/ListDB")
public class ListDB extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListDB() {
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
		PrintWriter out = response.getWriter();
		printResultsStart(out);

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

		/*
		 * loop through each customer in the database and create a customerAcct
		 * object to print if the database exists.
		 */

		DBCursor cursor = accounts.find();
		CustomerAcct tempCustomer = new CustomerAcct();

		while (cursor.hasNext()) {
			DBObject currentCustomer = cursor.next();
			tempCustomer.setCustomerAcct(Integer.valueOf(currentCustomer.get(
					"id").toString()));
			tempCustomer
					.setCustomerName(currentCustomer.get("name").toString());
			tempCustomer.setCustomerMoney(Double.parseDouble(currentCustomer
					.get("money").toString()));
			printResultsMiddle(out, tempCustomer);
		}

		printResultsEnd(out);

	}

	private void printResultsStart(PrintWriter out) {
		String title = "Database Contents";

		out.println("<HTML><HEAD><link rel=\"stylesheet\" href=\"css/style.css\"><TITLE>");
		out.println("<html><head><title>");
		out.println(title);
		out.println("</TITLE></HEAD><body><div class = 'container'>");
		out.println("<H1 align=\"center\">" + title + "</H1>");
		out.println("<BR><BR><BR>");

		out.println("<TABLE align='center' width=300px >");
		out.println("<TBODY align = 'left'>");
		out.println("<TR>");
		out.println("<TH width = '10%'>ID</TH>");
		out.println("<TH width = '60%'>Name </TH>");
		out.println("<TH width = '30%'>Balance </TH>");
		out.println("</TR>");
	}

	private void printResultsMiddle(PrintWriter out, CustomerAcct ca) {
		out.println("<TR>");
		out.println("<TD>" + ca.getCustomerAcct() + "</TD>");
		out.println("<TD>" + ca.getCustomerName() + "</TD>");
		out.println("<TD>"
				+ NumberFormat.getCurrencyInstance().format(
						ca.getCustomerMoney()) + "</TD>");
		out.println("</TR>");
	}

	private void printResultsEnd(PrintWriter out) {
		out.println("</TBODY>");
		out.println("</TABLE>");
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
