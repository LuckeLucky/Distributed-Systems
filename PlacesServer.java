import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PlacesServer {
    public static Registry r;
    public static void main(String[] args){
        Address service = new Address(args[0],Integer.parseInt(args[1]));
        Address multicast = new Address(args[2],Integer.parseInt(args[3]));

        PlacesManager place_manager;
        try{
            r = LocateRegistry.createRegistry(Integer.parseInt(args[1]));
        }catch( RemoteException a){
            a.printStackTrace();
        }

        try{
            place_manager = new PlacesManager(service,multicast);
            r.rebind("placesmanager", place_manager );

            System.out.println("Place server ready");
        }catch(Exception e) {
            System.out.println("Place server main " + e.getMessage());
        }
    }
}
