import java.applet.AppletContext;
import java.applet.AppletStub;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class RsAppleStub implements AppletStub {

    private String link = "http://oldschool12.runescape.com/";
    private HashMap<String, String> parameters = new HashMap<String, String>();


    public RsAppleStub() {
        parse();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public URL getDocumentBase() {
        return getCodeBase();
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {

    }

    private void parse() {
        try {
            System.out.println("Parsing Website");
            final URLConnection urlConnection = new URL(link + "l=0/jav_config.ws").openConnection();
            urlConnection.addRequestProperty("User-Agent", "Opera/9.80 (Windows NT 6.1; U; en-GB) Presto/2.10.229 Version/11.61");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                line = line.replaceAll("\">'", "\"").replaceAll("'", "")
                        .replaceAll("\\(", "").replaceAll("\\)", "")
                        .replaceAll("\"", "").replaceAll(" ", "")
                        .replaceAll("param=", "").replaceAll(";", "")
                        .replaceAll("value", "");
                final String[] splitted = line.split("=");
                if (splitted.length == 1) {
                    parameters.put(splitted[0], "");
                } else if (splitted.length == 2) {
                    parameters.put(splitted[0], splitted[1]);
                } else if (splitted.length == 3) {
                    parameters.put(splitted[0], splitted[1] + "=" + splitted[2]);
                } else if (splitted.length == 4) {
                    parameters.put(splitted[0], splitted[1] + "=" + splitted[2] + "=" + splitted[3]);
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}