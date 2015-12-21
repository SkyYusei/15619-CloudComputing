package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


import org.json.JSONArray;

public class ProfileServlet extends HttpServlet {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/p1";
	static final String USER = "root";
	static final String PASSWD = "15319project";
	static Connection conn = null;
	static Statement statement1 = null;
	static Statement statement2 = null;
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASSWD);
			System.out.println("Connected to database...");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    public ProfileServlet() {

    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        String id = request.getParameter("id");
        String pwd = request.getParameter("pwd");
        Boolean flag = false;
        String name = "Unauthorized";
        String url = "#";
        try {
        	//check whether the user is validate
        	statement1 = conn.createStatement();
        	String sql = "SELECT * FROM li WHERE id = "+id+" AND pwd = '"+pwd+"';";
        	System.out.println(sql);
        	ResultSet resultSet = statement1.executeQuery(sql);
        	while(resultSet.next()){
        		System.out.println("true");
        		statement2 = conn.createStatement();
        		String sql2 = "SELECT * FROM up WHERE id = "+id+";";
        		System.out.println(sql2);
        		ResultSet resultSet2 = statement2.executeQuery(sql2);
        		while(resultSet2.next()){
        			//if the user is validate
        			name = resultSet2.getString("name");
        			url = resultSet2.getString("url");
        			System.out.println("==="+name+"=="+url);
        		}
        		resultSet2.close();
        		statement2.close();
        	}
        	resultSet.close();
        	statement1.close();
      
		} catch (Exception e) {
			// TODO: handle exception
		}
               
        // implement the functionalities in doGet method.
        // you can add any helper methods or classes to accomplish this task
        // sample code: delete these code when you start
       
        result.put("name", name);
        System.out.println(name);
        System.out.println(url);
        result.put("profile", url);
        // sample code ends

        PrintWriter writer = response.getWriter();
        writer.write(String.format("returnRes(%s)", result.toString()));
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
