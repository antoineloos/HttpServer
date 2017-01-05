/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TP2Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
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

    public static void main(String[] args) {
        new TP2Server();
    }

    public TP2Server() {
        try {
            myServerSocket = new ServerSocket(11111);
        } catch (IOException ioe) {
            System.out.println("impossible de creer le server sur le port 11111");
            System.exit(-1);
        }

        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        System.out.println("Le serveur est lancé à : " + formatter.format(now.getTime()));

        ExecutorService pool = Executors.newFixedThreadPool(10);

        while (ServerOn) {
            try {

                Socket subSocket = myServerSocket.accept();

                pool.submit(new ClientServiceThread(subSocket));

            } catch (IOException ioe) {
                System.out.println("une erreur est survenue");
                ioe.printStackTrace();
            }

        }

        try {
            myServerSocket.close();
            System.out.println("Serveur Stoppé");
        } catch (Exception ioe) {
            System.out.println("Problème");
            System.exit(-1);
        }

    }

    class ClientServiceThread implements Runnable {

        Socket myClientSocket;
        boolean m_bRunThread = true;

        public ClientServiceThread() {
            super();
        }

        ClientServiceThread(Socket s) {
            super();
            myClientSocket = s;
        }

        public void run() {

            BufferedInputStream in = null;
            BufferedOutputStream out = null;

            System.out.println(" Nouvelle requète de " + myClientSocket.getInetAddress().getHostName());

            try {
                in = new BufferedInputStream(myClientSocket.getInputStream());
                out = new BufferedOutputStream(myClientSocket.getOutputStream());
                /*while(m_bRunThread) 
                 {    */
                //String clientCommand = in.readLine();
                //System.out.println("Le client dit :" + clientCommand);
                if (!ServerOn) {

                    System.out.print("Serveur a été stoppé");
                    out.write("Serveur a été stoppé".getBytes());
                    out.flush();
                    m_bRunThread = false;

                }

                String request = "";
                byte[] buffer;
                int tempo = 100;
                while (in.available() == 0 && tempo > 0) {
                    sleep(1);
                    //System.out.println(tempo);
                    tempo--;
                }
                while (in.available() > 0) {
                    buffer = new byte[in.available()];
                    in.read(buffer);
                    String bufferstring = new String(buffer);
                    System.out.println(bufferstring);
                    request += bufferstring;
                }
                Calendar debut = Calendar.getInstance();
                long debmillis = debut.getTimeInMillis();
                //if (request.contains("GET")) {
                //System.out.println(request);
                Matcher m = Pattern.compile("^GET \\/(.*) ").matcher(request);
                
                if (m.find()) {
                    String nomfichier = m.group(1);
                    System.out.println(nomfichier);
                    File f = new File(nomfichier);
                    String code;
                    if (f.exists() && !f.isDirectory()) {
                        code = "200 OK";
                    } else {
                        System.out.println("[Erreur] - 404 : Not Found.");
                        code = "404 Not Found";
                        f = new File("404.html");
                    }
                    FileInputStream fs = new FileInputStream(f);
                    long size = f.length();
                    String header = getHeader(code, f, size);
                    out.write(header.getBytes());

                    int buffersize = fs.available();
                    while (buffersize > 0) {
                        if (buffersize > 512) {
                            buffersize = 512;
                        }
                        buffer = new byte[buffersize];
                        fs.read(buffer);
                        out.write(buffer);
                        buffersize = fs.available();
                    }
                    fs.close();
                } else {
                    System.out.println("[Erreur] - 400 : Bad Request.");
                    String header = getHeader("400 Bad Request");
                    String response = header + "<h2>Erreur 400 - Requète mal formattée.</h2>";
                    out.write(response.getBytes());
                }

                out.flush();
                Calendar fin = Calendar.getInstance();
                long finmillis = fin.getTimeInMillis();
                long tempsmillis = finmillis-debmillis;
                double tempssec = tempsmillis/(double)1000;
                System.out.println("Transfert terminé. Temps de réponse : "+String.valueOf(tempssec)+"s.");
                

                //}
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                // Clean up 
                try {
                    in.close();
                    out.close();
                    myClientSocket.close();
                    System.out.println("...Stop");
                } catch (IOException ioe) {
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
        switch (extension) {
            case "jpg":
            case "jpeg":
                result = "image/jpeg";
                break;
            case "png":
                result = "image/png";
                break;
            case "css":
                result = "text/css";
                break;
            case "js":
                result = "application/javascript";
                break;
            case "pdf":
                result = "application/pdf";
                break;
            case "avi":
                result = "video/x-msvideo";
                break;
            case "mkv":
                result = "video/webm";
                break;
            case "mp4":
                result = "video/mp4";
                break;
            default:
                break;
        }
        return result;
    }

    private String getHeader(String code, File f, long size) {
        String response = "HTTP/1.1 " + code + "\r\n"
                + "Server: BoLoos Server (Win64)\r\n"
                + "Content-Type: " + getMimeType(f.getName()) + "\r\n"
                + "Filename: " + f.getName() + "\r\n"
                + "Content-length: " + size + "\r\n\r\n";
        return response;
    }
    
    private String getHeader(String code) {
        String response = "HTTP/1.1 " + code + "\r\n"
                + "Server: BoLoos Server (Win64)\r\n"
                + "Content-Type: text/html\r\n\r\n";
        return response;
    }

}
