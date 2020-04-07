package greet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;


public class GreetService extends HttpServlet {

   String greetings[] = new String[4];

   public GreetService()
   {
	   //fill in some data
	   greetings[0] = "good morning";
	   greetings[1] = "guten Morgen";
	   greetings[2] = "bonjour";
	   greetings[3] = "bon giorno";
   }

   private String parseURI(String uri)
   {
     //this is an simple URI parser. It will take the
     //incoming URI and strip useful information from it.
     //the URI is the unique resource identifier and it is
     //part of the URL, the unique resource locator.
     //an example:
     //URL:  localhost:8080/wsvex/greet/en
     //the the URI would in this case be:
     //wsvex/greet/en
     //so what we need here is the "en" and therefore we
     //have to strip off the rest

	   String retVal = new String("");
	   StringTokenizer tk = new StringTokenizer(uri,"/");
	   tk.nextToken();   //we dont need this (wsvex)
	   tk.nextToken();   //we dont need this (greet)
	   if (tk.hasMoreTokens())
	   {
		   retVal = tk.nextToken(); //this will be one of en,de,fr,it
	   }
	   return retVal;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
      // Set the response message's MIME type
	      response.setContentType("text/plain; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      //out.println("[{\"method\":\"get\"},");
      String rqValue = parseURI(request.getRequestURI());

      try {
	    String result = new String("");
	    if (rqValue.isEmpty())
	    {
            for(int i=0;i<4;i++){
                result+=(greetings[i]+"\n");
            }
	    }
	    else
	    {
            switch(rqValue){
                case "en":
                    result=greetings[0];
                break;
                case "de":
                    result=(greetings[1]);
                break;
                case "fr":
                    result=(greetings[2]);
                break;
                case "it":
                    result=(greetings[3]);
                break;
                default:
                    result="Language does not exist";
                break;                                  
            }
	    }
	  	out.println(result);
      } finally {
         out.close();  // Always close the output writer
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
        doGet(request, response);  
   }

   public void doPut(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
      // Set the response message's MIME type
	    response.setContentType("text/plain; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      String rqValue = parseURI(request.getRequestURI());
      //read what is in the data
      BufferedReader reader = request.getReader();
      String newGreetVerb = new String("");
      newGreetVerb = reader.readLine();
      //now we know the language (rqValue) and the new greeting (newGreetVerb)

      try
      {
	    String result = new String("");
	    if (rqValue.isEmpty())
	    {
            result="Please specify a language";
	    }
	    else
	    {
            switch(rqValue){
                case "en":
                    greetings[0]=newGreetVerb;
                    result=greetings[0];
                break;
                case "de":
                    greetings[1]=newGreetVerb;
                    result=(greetings[1]);
                break;
                case "fr":
                    greetings[2]=newGreetVerb;
                    result=(greetings[2]);
                break;
                case "it":
                    greetings[3]=newGreetVerb;
                    result=(greetings[3]);
                break;
                default:
                    result="Language does not exist";
                break;  
            }
	    }
	  	out.println(result+"\n");
      } 
      finally 
      {
         out.close();  // Always close the output writer
      }
   }
}

