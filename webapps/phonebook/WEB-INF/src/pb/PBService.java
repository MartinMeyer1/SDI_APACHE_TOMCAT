package pb;
 
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PBR
{
	private String name = "";
	private String phone ="";
	//default constructor
	public PBR()
	{
		this.name = "";
		this.phone = "";
	}	
	//getter for name
	public String getName()
	{
		return this.name;
	}
	//getter for phone number
	public String getPhone()
	{
		return this.phone;
	}
    //convert to JSON object
	public String toJSON()
	{
		return "{\"name\":\"" + name + "\",\"phone\":\"" + phone + "\"}";
	}
	//convert from JSON object
	public void fromJSON(String jsonStr)
	{
		jsonStr = replace(jsonStr, "{}");
		StringTokenizer tk = new StringTokenizer(jsonStr, ",");
		String t1=replace(tk.nextToken(),"\"");
		String t2=replace(tk.nextToken(),"\"");		
		tk = new StringTokenizer(t1,":");
		tk.nextToken();
		this.name=tk.nextToken();
		tk = new StringTokenizer(t2,":");
		tk.nextToken();
		this.phone=tk.nextToken();
	}
	//helper for conversion from JSON object
	private String replace(String text, String charsToBeReplaced) {
        Pattern p = Pattern.compile("(.{1})");
        Matcher m = p.matcher(charsToBeReplaced);
        return text.replaceAll(m.replaceAll("\\\\$1\\|"), "");
    }
}

class Database 
{
	//this is the database
	private HashMap	<Integer,PBR> data = new HashMap<Integer,PBR>();
	//generate next unique key
	private Integer nextKey()
	{
		Integer key = new Integer(0);
		for (Map.Entry it : data.entrySet())
		{
          if ((Integer)it.getKey() > key)
          {
	          key = (Integer) it.getKey();
          } 
        }		
        System.out.println(key);
		return ++key;
	}
	//add a key-json object pair
	public void addPBR(String pbrJson)
	{
		PBR pbr = new PBR();
		pbr.fromJSON(pbrJson);
		data.put(nextKey(), pbr);
	}
	//remove first found phonebook entry for a given name
	public boolean removePBR(String name)
	{
		boolean retVal = false;
		Integer k = new Integer(-1);
        Iterator iterator = data.entrySet().iterator();
        while (iterator.hasNext() && k == -1) {
            Map.Entry it = (Map.Entry) iterator.next();
            PBR p = (PBR) it.getValue();
			if (p.getName().equals(name))
			{
				k = (Integer) it.getKey();
			}
		}		
		if (k != -1)
		{
			data.remove(k);
			retVal = true;
		}
		return retVal;
	}
	//modify a phonebook entry
	public boolean modifyPBR(String oldPbrJson, String newPbrJson)
	{
		boolean retVal = false;
		Integer k = new Integer(-1);
		PBR pbr = new PBR();
		pbr.fromJSON(oldPbrJson);
        Iterator iterator = data.entrySet().iterator();
        while (iterator.hasNext() && k == -1) {
            Map.Entry it = (Map.Entry) iterator.next();
            PBR p = (PBR) it.getValue();
			if (p.getName().equals(pbr.getName()) && 
			    p.getPhone().equals(pbr.getPhone()))
			{
				k = (Integer) it.getKey();
			}
		}		
		if (k != -1)
		{
			PBR newPbr = new PBR();
			newPbr.fromJSON(newPbrJson);
			data.put(k,newPbr);
			retVal = true;
		}
		return retVal;
	}
	//return json object array with all entries
	public String getAllPBR()
	{
		String s = new String("{\n\"phonebook\":\n[\n");
		boolean colon = false;
		for (Map.Entry it : data.entrySet()) 
		{
			if (colon) s+=",\n";
			else colon = true;
			PBR p = (PBR) it.getValue();
			s+=p.toJSON();
        }
		s+="\n]\n}";		
		return s;
	}
	//return json object array with all entries for a given name
	public String getPBR(String name)
	{
		String s = new String("{\n\"Entries for "+name+"\":\n[\n");
		boolean colon = false;
		for (Map.Entry it : data.entrySet()) 
		{
			PBR p = (PBR) it.getValue();
			if (p.getName().equals(name))
			{
			  if (colon) s+=",\n";
			  else colon = true;
			  s+=p.toJSON();
			}
        }
		s+="\n]\n}";		
		return s;
	}
}
 
public class PBService extends HttpServlet {
 
   Database pb = new Database();
   
   public PBService()
   {
	   //fill in some data
	   pb.addPBR("{\"name\":\"Jon\",\"phone\":\"0561111111\"}");
	   pb.addPBR("{\"name\":\"Rose\",\"phone\":\"0542222222\"}");
	   pb.addPBR("{\"name\":\"Jon\",\"phone\":\"0113333333\"}");
	   pb.addPBR("{\"name\":\"Spike\",\"phone\":\"0554444444\"}"); 
   }
   
   private String parseURI(String uri)
   {
	   String retVal = new String("");
	   StringTokenizer tk = new StringTokenizer(uri,"/");
	   tk.nextToken();   //we dont need this (phonebook)
	   tk.nextToken();   //we dont need this (pbservice)
	   if (tk.hasMoreTokens())
	   {
		   retVal = tk.nextToken(); //this will eg be jon
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
      String rqName = parseURI(request.getRequestURI());
      
      try {
	    String result = new String("");  
	    if (rqName.isEmpty())
	    {
		    result = pb.getAllPBR();
	    }
	    else
	    {
		    result = pb.getPBR(rqName);
	    }
	    //result+="]";
	  	out.println(result);
      } finally {
         out.close();  // Always close the output writer
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
   	  // Set the response message's MIME type
      response.setContentType("text/plain; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      out.println("modify record\n");
	  try {
		String oldPBRjson = null;
		String newPBRjson = null;
        BufferedReader reader = request.getReader();
        oldPBRjson = reader.readLine();
        newPBRjson = reader.readLine();
		out.println("\nold value:\n"+ oldPBRjson);
		out.println("\nnew value:\n"+ newPBRjson);
	  	if (pb.modifyPBR(oldPBRjson,newPBRjson))
	  	{
		  	out.println("\nsucess");
	  	}
	  	else
	  	{
		  	out.println("fail");		  	
	  	}
      } finally {
         out.close();  // Always close the output writer
      }
   }
   @Override
   public void doPut(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
   	  // Set the response message's MIME type
      response.setContentType("text/plain; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      out.println("add record\n");
	  try {
		String PBRjson = null;
        BufferedReader reader = request.getReader();
        PBRjson = reader.readLine();
		out.println(PBRjson);
	  	pb.addPBR(PBRjson);
	  	out.println("\nsucess");
      } finally {
         out.close();  // Always close the output writer
      }
   }
   @Override
   public void doDelete(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
   	  // Set the response message's MIME type
      response.setContentType("text/plain; charset=UTF-8");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter(); 
           
      String rqName = parseURI(request.getRequestURI());  
      try {
	    String result = new String("");  
	    if (rqName.isEmpty())
	    {
		    result = "FAIL, no name specified";
	    }
	    else
	    {
		    if (pb.removePBR(rqName))
		    {
			    result = "SUCESS";
		    }
		    else
		    {
			    result = "FAIL, " + rqName + " not found";
		    }
	    }
	  	out.println(result);
      } finally {
         out.close();  // Always close the output writer
      }
   }

 }
 
 