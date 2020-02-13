import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FrontEnd {
    public static Registry r;
    public static void main(String[] args){

        Address multicast = new Address(args[2],Integer.parseInt(args[3]));

        FrontEndService front_end_service;
        try{
            r = LocateRegistry.createRegistry(Integer.parseInt(args[1]));
        }catch( RemoteException a){
            a.printStackTrace();
        }

        try{
            front_end_service = new FrontEndService(multicast);
            r.rebind("frontend", front_end_service );

            System.out.println("FrontEnd server ready");
        }catch(Exception e) {
            System.out.println("Place server main " + e.getMessage());
        }
    }
}
