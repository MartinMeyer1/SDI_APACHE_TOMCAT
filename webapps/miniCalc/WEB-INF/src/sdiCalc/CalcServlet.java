package sdiCalc;
 
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
 
public class CalcServlet extends HttpServlet {
 
   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
      // Set the response message's MIME type
      response.setContentType("text/html; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
 
      // Write the response message, in an HTML page
      try {
         out.println("<!DOCTYPE html>");
         out.println("<html><head>");
         out.println("<title>Caclulator Servlet</title></head>");
         out.println("<body><h1>Here comes the result</h1><br><hr>");
 
         // Retrieve the value of the query parameter "operandA" 
         String operandA = request.getParameter("operandA");
         out.println("<p>"+operandA + " +");
         // Retrieve the value of the query parameter "operandB" 
         String operandB = request.getParameter("operandB");
         out.println(operandB); 
         float a = Float.valueOf(operandA);
         float b = Float.valueOf(operandB);
         out.println("= <strong>" + (a+b) + "</strong><hr><br><br>");
         // Hyperlink "BACK" to input page
         out.println("<a href='calc.html'>BACK</a>");
         out.println("</body></html>");
      } finally {
         out.close();  // Always close the output writer
      }
   }
   // Redirect POST request to GET request.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
      doPost(request, response);
   }
 }
 
 