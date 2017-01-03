/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TP2Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
 import java.util.regex.Pattern;

/**
 *
 * @author Epulapp
 */

public class TP2Server {

    ServerSocket myServerSocket;
    boolean ServerOn = true;

    public static void main (String[] args) 
    { 
        new TP2Server();        
    } 
    
    public TP2Server() 
    { 
        try 
        { 
            myServerSocket = new ServerSocket(1111); 
        } 
        catch(IOException ioe) 
        { 
            System.out.println("impossible de creer le server sur le port 1111"); 
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
          
            BufferedInputStream in = null; 
            BufferedOutputStream out = null; 

            System.out.println(" Nouvelle requète de " + myClientSocket.getInetAddress().getHostName()); 

            try 
            {                                
                in = new BufferedInputStream(myClientSocket.getInputStream());
                out = new BufferedOutputStream(myClientSocket.getOutputStream());

                
                /*while(m_bRunThread) 
                {    */                
                  
                    //String clientCommand = in.readLine();
                    //System.out.println("Le client dit :" + clientCommand);
                    
                    if(!ServerOn) 
                    { 
                       
                        System.out.print("Serveur a été stoppé"); 
                        out.write("Serveur a été stoppé".getBytes()); 
                        out.flush(); 
                        m_bRunThread = false;   

                    }
                    
                    String request = "";
                    byte[] buffer;
                    
                    while(in.available() > 0) {
                        buffer = new byte[512];
                        in.read(buffer);
                        String bufferstring = new String(buffer);
                        request += bufferstring;
                    }
                    if (request.contains("GET")) {
                        //System.out.println(request);
                        Matcher m = Pattern.compile("GET \\/(.*) ").matcher(request);
                        m.find();
                        String nomfichier = m.group(1);
                        System.out.println(nomfichier);
                        File f = new File(nomfichier);
                        String code;
                        if(f.exists() && !f.isDirectory()) {
                            System.out.println("Fichier trouvé!");
                            code = "200 OK";
                        } else {
                            System.out.println("Fichier non trouvé!");
                            code = "404";
                            f = new File("404.html");
                        }
                        FileInputStream fs = new FileInputStream(f);
                        long size = f.length();
                        String header = "HTTP/1.1 " + code + "\n" +
                                        "Server: BoLoos Server (Win64)\n" +
                                        "Content-Type: "+getMimeType(f.getName())+"\n" +
                                        "Filename: "+f.getName()+"\n" +
                                        "Content-length: "+size+"\n\n";
                        out.write(header.getBytes());

                        int buffersize = fs.available();
                        while (buffersize > 0){
                            buffer = new byte[buffersize];
                            fs.read(buffer);
                            out.write(buffer);
                            buffersize = fs.available();
                        }
                        fs.close();
                        out.flush();
                    

                    
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
    
    private String getMimeType(String filename) {
        String result = "text/html";
        Matcher m = Pattern.compile("^.*\\.([a-zA-Z0-9]*)$").matcher(filename);
        String extension;
        if (m.find()) {
            extension = m.group(1);
        } else {
            return result;
        }
        extension = extension.toLowerCase();
        //System.out.println("Extension : "+extension);
        switch(extension) {
            case "jpg" :
            case "jpeg" :
                result = "image/jpeg";
                break;
            case "png" :
                result = "image/png";
                break;
            case "css" :
                result = "text/css";
                break;
            case "js" :
                result = "application/javascript";
                break;
            default :
                break;
        }
        return result;
    } 

}
