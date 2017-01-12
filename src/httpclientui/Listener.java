/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpclientui;

/**
 *
 * @author Epulapp
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Epulapp
 */
public class Listener implements Runnable {

    private BufferedInputStream in;
    private PrintWriter out;
    private Socket courantSocket;
    private Boolean ServerOn = true;
    private Boolean m_bRunThread = true;

    public Listener(Socket s, PrintWriter o) {
        courantSocket = s;
        out = o;
    }

    @Override
    public void run() {

        try {
        
            in = new BufferedInputStream(courantSocket.getInputStream());
            
          
            
            while (m_bRunThread) {

                    //String clientCommand = in.readLine();
                //System.out.println("Le client dit :" + clientCommand);
                if (!ServerOn) {

                    System.out.print("Serveur a été stopé");
                    System.out.println("Serveut a été stopé");

                    m_bRunThread = false;

                }

                String request = "";
                 
                ByteArrayOutputStream BS = new ByteArrayOutputStream();
                byte[] buffer;
                
                int cmpt = 0;

                while (in.available() > 0) {
                    int buffersize = in.available();
                    if (buffersize > 4096) {
                        buffersize = 4096;
                    }
                    buffer = new byte[buffersize];
                    in.read(buffer);
                    
                    BS.write(buffer);
                   
                    String bufferstring = new String(buffer);
                    request += bufferstring;
                }
               
                Matcher m = Pattern.compile("HTTP/1.1 ([0-9]+) [a-z\\sA-Z]*").matcher(request);
                
                if (m.find()) {
                    //System.out.println(request);
                    String code = m.group(1);
                    switch (Integer.parseInt(code)) {
                        case 200: {
                           System.out.println("200 code catch");
                            //Matcher m2 = Pattern.compile("^((.|\\n)+)\\n\\n((.|\\n)*)").matcher(request);
                            String[] res = request.split("\\r\\n\\r\\n");
                            String filename = "a.txt";
                            
                            if(res.length>1)
                            {
                            String header = res[0];
                            byte[] data = BS.toByteArray();
                            Matcher m3 = Pattern.compile("Filename\\s*:\\s*(.+)").matcher(header);
                            if(m3.find()) filename = m3.group(1);
                            FileOutputStream fs = new FileOutputStream(filename);
                            int headerLenght = header.getBytes().length;
                            int reqLenght = data.length;
                            System.out.println("file writing begin");
                            for (int i = headerLenght + ("\\r\\n").getBytes().length; i < reqLenght; i++) {
                                
                                
                                fs.write(data[i]);
                                
                            }
                            System.out.println("file creation finished");
                            fs.close();
                            }
                            
                            
                            break;
                        }
                        case 301: {
                            System.out.println("permanently redirected");
                            break;
                        }
                        case 302: {
                            System.out.println("temporary redirected");
                            break;
                        }

                        case 400: {
                            System.out.println("bad request");
                            break;
                        }
                        case 401: {
                            System.out.println("user acces restriction");
                            break;
                        }
                        case 403: {
                            System.out.println("acces file denied");
                            break;
                        }
                        case 404: {
                            System.out.println("file not found");
                            break;
                        }
                        case 500: {
                            System.out.println("erreur server");
                            break;
                        }
                        case 503: {
                            System.out.println("erreur server");
                            break;
                        }

                    }
                }
            }

            in.close();
            out.close();
            courantSocket.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
            out.close();
        } finally {
                // Clean up 
            
            out.close();
            try {
                courantSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("...Stop");

        }

    }
}

