import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(final String args[]) {
        Thread t1;
        t1 = (new Thread(() -> {
            PlacesServer.main(new String[]{"localhost","2001","224.0.0.1", "5000"});
            PlacesServer.main(new String[]{"localhost","2002","224.0.0.1", "5000"});
            PlacesServer.main(new String[]{"localhost","2003","224.0.0.1", "5000"});
            PlacesServer.main(new String[]{"localhost","2004","224.0.0.1", "5000"});
            PlacesServer.main(new String[]{"localhost","2005","224.0.0.1", "5000"});

        }));
        t1.start();

        try {
            Thread.sleep(20000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        Place p1 = new Place("3520-011","Nelas");
        Place p2 = new Place("3250-022","Viseu");
        PlacesListInterface pli;

        try {
            pli=(PlacesListInterface) Naming.lookup("rmi://"+args[0]+":"+args[1]+"/frontend");
            pli.addPlace(p1);
            pli.addPlace(p2);
            Thread.sleep(5000);
            pli.getPlace("3520-011");
            pli.getPlace("3520-011");
            pli.removePlace("3250-022");
        } catch (NotBoundException | MalformedURLException | RemoteException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}