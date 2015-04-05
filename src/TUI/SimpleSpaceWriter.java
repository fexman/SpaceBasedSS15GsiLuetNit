package TUI;

import org.mozartspaces.core.*;

import java.net.URI;
import java.util.Scanner;

import static SXvsm.XvsmUtil.lookUpOrCreateContainer;

/**
 * Created by Felix on 05.04.2015.
 */
public class SimpleSpaceWriter {

    public static final URI SPACE = URI.create("xvsm://localhost:12345");
    public static Capi capi;

    public static void main(String[] args) {

        //Holt sich die Sync-Api Instanz
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        System.out.println("Welcome to the text-based space writer!");
        System.out.println("\tUsage: <container> <entry>");
        Scanner scan = new Scanner(System.in);

        //Loop die input einliest
        while(scan.hasNextLine()) {
            String rawInput = scan.nextLine();
            String[] input = rawInput.split(" ");
            if (input.length == 2) {
                String response;
                ContainerReference cref = null;

                //Container holen oder neu erstellen
                try {
                    cref = lookUpOrCreateContainer(input[0], SPACE, capi);
                } catch (MzsCoreException e) {
                    System.out.println("Could not look up or create container: " + e.getMessage());
                }

                //Versuchen Entry zu schreiben
                try {
                    capi.write(cref,new Entry(input[1]));
                } catch (MzsCoreException e) {
                    System.out.println("Could write to container: " + e.getMessage());
                }

                //Feedback an User (absichtlich lokal, für remote Feedback nutze SimpleSpaceViewer)
                System.out.println("Wrote "+input[1]+" to "+input[0]);
            } else {
                //Invalid Input
                System.out.println("Invalid input!");
                System.out.println("\tUsage: <container> <entry>");
            }
        }
    }


}
