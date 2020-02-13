import java.io.Serializable;

public abstract class Message implements Serializable {


    private Address from;
    private Address to;
    private Integer term;

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public abstract String getType();

    public String toString(){
        return "from "+this.from+" to "+this.getTo()+" tipo "+this.getType()+ " no termo "+this.term;
    }
}
