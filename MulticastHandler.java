import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastHandler{
    private String multicast_address;
    private int multicast_port;

    public MulticastHandler(Address multicast_address) {
        this.multicast_address = multicast_address.getAddress();
        this.multicast_port = multicast_address.getPort();
    }

    public Message listenMulticastMessages() throws IOException, ClassNotFoundException {

        //Create Socket
        InetAddress group = InetAddress.getByName(this.multicast_address);
        MulticastSocket socket = new MulticastSocket(this.multicast_port);
        socket.joinGroup(group);


        //Create buffer
        byte[] buffer = new byte[1024];
        socket.receive(new DatagramPacket(buffer, 1024, group, this.multicast_port));

        //Deserialize object
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);

        Message message = (Message) ois.readObject();

        //Close socket
        socket.close();

        return message;

    }

    public  void sendMulticastMessage(Message message) throws IOException {
        //Create socket
        MulticastSocket socket = new MulticastSocket();
        InetAddress group = InetAddress.getByName(this.multicast_address);
        socket.joinGroup(group);

        //Prepare data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        byte[] data = baos.toByteArray();

        //Send data
        socket.send(new DatagramPacket(data, data.length, group, this.multicast_port));

        //Close socket
        socket.close();

    }


}
