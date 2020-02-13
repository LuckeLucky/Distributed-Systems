import java.io.Serializable;

public class Address implements Serializable {
    private String address;
    private Integer port;

    public Address(String address, Integer port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }


    public Integer getPort() {
        return port;
    }


    public boolean equals(Address address){
        return ( this.getAddress().equals(address.getAddress()) && this.getPort().equals(address.getPort()));
    }

    public String toString(){
        return this.getAddress()+":"+ this.getPort();
    }

}
