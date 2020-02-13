import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PlacesListInterface extends Remote {
    void addPlace(Place p)  throws RemoteException;
    void removePlace(String objectID) throws RemoteException;
    ArrayList allPlaces() throws RemoteException;
    Place getPlace(String objectID) throws RemoteException;
}