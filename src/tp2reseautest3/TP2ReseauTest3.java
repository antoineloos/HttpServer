/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp2reseautest3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Epulapp
 */

public class TP2ReseauTest3 {

    ServerSocket myServerSocket;
    boolean ServerOn = true;

    public static void main (String[] args) 
    { 
        new TP2ReseauTest3();        
    } 
    
    public TP2ReseauTest3() 
    { 
        try 
        { 
            myServerSocket = new ServerSocket(11111); 
        } 
        catch(IOException ioe) 
        { 
            System.out.println("impossible de creer le server sur le port 11111"); 
            System.exit(-1); 
        } 




        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        System.out.println("Le serveur est lancé à : " + formatter.format(now.getTime()));



        ExecutorService pool = Executors.newFixedThreadPool(10);
         
        while(ServerOn) 
        {                        
            try 
            { 
                
                Socket subSocket = myServerSocket.accept();
                
                pool.submit(new ClientServiceThread(subSocket));
                 

            } 
            catch(IOException ioe) 
            { 
                System.out.println("une erreure est survenue"); 
                ioe.printStackTrace(); 
            } 

        }

        try 
        { 
            myServerSocket.close(); 
            System.out.println("Serveur Stoppé"); 
        } 
        catch(Exception ioe) 
        { 
            System.out.println("Problème"); 
            System.exit(-1); 
        } 



    } 

    


    class ClientServiceThread implements Runnable
    { 
        Socket myClientSocket;
        boolean m_bRunThread = true; 

        public ClientServiceThread() 
        { 
            super(); 
        } 

        ClientServiceThread(Socket s) 
        { 
            myClientSocket = s; 

        } 

        public void run() 
        {            
          
            BufferedReader in = null; 
            PrintWriter out = null; 

            
           
            System.out.println(" Adresse du Client - " + myClientSocket.getInetAddress().getHostName()); 

            try 
            {                                
                in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream())); 
                out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream())); 

                
                while(m_bRunThread) 
                {                    
                  
                    String clientCommand = in.readLine(); 
                    System.out.println("Le client dit :" + clientCommand);

                    if(!ServerOn) 
                    { 
                       
                        System.out.print("Serveur a été stopé"); 
                        out.println("Serveut a été stopé"); 
                        out.flush(); 
                        m_bRunThread = false;   

                    } 

                    if(clientCommand.equalsIgnoreCase("quit")) { 
                       
                        m_bRunThread = false;   
                        System.out.print("Stopper le thread client : "); 
                    } else if(clientCommand.equalsIgnoreCase("end")) { 
                    
                        m_bRunThread = false;   
                        System.out.print("Stopper le thread client : "); 
                        ServerOn = false;
                    } else {
                            
                            out.println("Le serveur dit : " + clientCommand); 
                            out.flush(); 
                    }
                } 
            } 
            catch(Exception e) 
            { 
                e.printStackTrace(); 
            } 
            finally 
            { 
                // Clean up 
                try 
                {                    
                    in.close(); 
                    out.close(); 
                    myClientSocket.close(); 
                    System.out.println("...Stop"); 
                } 
                catch(IOException ioe) 
                { 
                    ioe.printStackTrace(); 
                } 
            } 
        } 


    } 
}
