package br.com.elvisoliveira.css.mockup;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

public class Default
{

    private static final Timer timer = new Timer();
    private static final Integer delay = 5000;   // delay de 5 seg.
    private static final Integer interval = 1000;  // intervalo de 1 seg.

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public static void main(String[] args)
    {
        try
        {
            final String currentfile = Default.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

            final String currentfolder = new File(currentfile).getParent();
            
            System.out.println("Current Folder: " + currentfolder);

            final HashMap<String, String> hmap = new HashMap<>();

            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    File folder = new File(currentfolder);
                    File[] listOfFiles = folder.listFiles();

                    for (File listOfFile : listOfFiles)
                    {
                        if (listOfFile.isFile() && FilenameUtils.getExtension(listOfFile.getAbsolutePath()).equals("css"))
                        {
                            String lastModDate = sdf.format(listOfFile.lastModified());

                            if (!hmap.containsKey(listOfFile.getName()))
                            {
                                hmap.put(listOfFile.getName(), sdf.toString());
                            }

                            if (!hmap.get(listOfFile.getName()).equals(lastModDate))
                            {
                                try
                                {
                                    Path path = Paths.get(listOfFile.getAbsolutePath());
                                    Charset charset = StandardCharsets.UTF_8;

                                    String content = new String(Files.readAllBytes(path), charset);
                                    content = content.replaceAll("\n", "");
                                    content = content.replaceAll("\r", "");

                                    for (int i = 0; i < 5; i++)
                                    {
                                        content = content.replaceAll("  ", " ");
                                    }

                                    content = content.replaceAll("}", "}\n");

                                    Files.write(path, content.getBytes(charset));

                                    hmap.put(listOfFile.getName(), sdf.format(listOfFile.lastModified()));

                                    System.out.println("Modified File: " + lastModDate + " " + listOfFile.getName());
                                }
                                catch (IOException ex)
                                {
                                    Logger.getLogger(Default.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }, delay, interval);
        }
        catch (URISyntaxException ex)
        {
            Logger.getLogger(Default.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
