import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.*;


public class Main extends JFrame
{
    private static final int WIDTH = 765;
    private static final int HEIGHT = 503;
    private static URL PATH = ClassLoader.getSystemClassLoader().getResource("190-deob.jar");

    public Main() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.setSize(800, 600);
        ClassLoader classLoader = new URLClassLoader(new URL[] {PATH});
        this.setTitle("Oak's Deob 0% Leech Edition");

        Class<?> clientClass = classLoader.loadClass("client");
        Applet client = (Applet) clientClass.newInstance();
        client.setStub(new RsAppleStub());
        this.add(client);
        client.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        client.init();
        this.setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        new Main();
    }
}