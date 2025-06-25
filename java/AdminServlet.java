package com.flippr.servlets;

import com.flippr.utils.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/admin")
@MultipartConfig // to handle file uploads
public class AdminServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "addProject":
                handleAddProject(request, response);
                break;
            case "addClient":
                handleAddClient(request, response);
                break;
            default:
                response.getWriter().write("Unknown action: " + action);
        }
    }

    private void handleAddProject(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        Part imagePart = request.getPart("image");

        String fileName = extractFileName(imagePart);
        String uploadPath = getServletContext().getRealPath("/") + "uploads/projects/";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        imagePart.write(uploadPath + fileName); // save image file

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO projects (name, description, image) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, "uploads/projects/" + fileName);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().write("Project added successfully.");
    }

    private void handleAddClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String designation = request.getParameter("designation");
        Part imagePart = request.getPart("image");

        String fileName = extractFileName(imagePart);
        String uploadPath = getServletContext().getRealPath("/") + "uploads/clients/";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        imagePart.write(uploadPath + fileName);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO clients (name, description, designation, image) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, designation);
            stmt.setString(4, "uploads/clients/" + fileName);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().write("Client added successfully.");
    }

    private String extractFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String piece : header.split(";")) {
            if (piece.trim().startsWith("filename")) {
                return piece.substring(piece.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown.png";
    }
}
