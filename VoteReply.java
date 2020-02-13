public class VoteReply extends Message {

    private boolean granted;

    @Override
    public String getType() {
        return "VoteReply";
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }
}
