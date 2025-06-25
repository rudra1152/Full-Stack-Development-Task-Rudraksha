package com.flippr.servlets;

import com.flippr.utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/contact")
public class ContactFormServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String city = request.getParameter("city");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO contact_forms (fullname, email, mobile, city) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullname);
            stmt.setString(2, email);
            stmt.setString(3, mobile);
            stmt.setString(4, city);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

 
        response.setContentType("text/html");
        response.getWriter().write("Contact form submitted successfully!");
    }
}
