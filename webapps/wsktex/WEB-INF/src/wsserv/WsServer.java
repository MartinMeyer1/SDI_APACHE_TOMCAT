//This sample is how to use websocket of Tomcat7.0.52.
//web.xml is not required. Because you can use Servlet3.0 Annotation.
package wsserv;

import java.io.IOException;
import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Random;

class SendDataTask extends java.util.TimerTask
{
    private WsServer srv = null;
    public SendDataTask(WsServer srv)
    {
        this.srv = srv;
    }
    
    public void run()
    {
        srv.sendData();
    }
}


@ServerEndpoint(value = "/wssrv")  //servlet 3.0 annotation

public class WsServer	{
    //notice:not thread-safe
    private static ArrayList<Session> sessionList = new ArrayList<Session>();
    private static SendDataTask sdt;
    private static Random r;
    private static java.util.Timer t;
    private static boolean timerIsScheduled = false;
    
    public WsServer(){
        r = new Random();
        t = new java.util.Timer();
        sdt = new SendDataTask(this);
        
        if(!timerIsScheduled){
            t.schedule(sdt,5000,5000);
            timerIsScheduled = true;
        }
    }
    
    public void sendData()
    {
        for(Session session : sessionList)
        {
            int num = r.nextInt(1000);
            String data = "Random number: " + String.valueOf(num);
            try{
                session.getBasicRemote().sendText(data);                
            }
            catch(IOException e){}
        }        
    }
    
    @OnOpen
    public void onOpen(Session session){
        try{
            sessionList.add(session);
            session.getBasicRemote().sendText("Welcome");
            session.getBasicRemote().sendText("Yout id is:" + session.getId());
        }catch(Exception e){}
    }
    
    @OnClose
    public void onClose(Session session){
        sessionList.remove(session);
    }
    
    @OnMessage
    public void onMessage(String msg,Session session){
	    if (msg.equals("data")){
        try{
        	session.getBasicRemote().sendText("You just clicked the button!");    
        }
        catch(Exception e){}}
    }
}

	