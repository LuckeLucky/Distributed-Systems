public class AppendRequest extends Message {

    private Change change;
    private int commit_index;

    @Override
    public String getType() {
        return "AppendRequest";
    }

    public Change getChange() {
        return change;
    }

    public void setChange(Change change) {
        this.change = change;
    }

    public int getCommitIndex() {
        return commit_index;
    }

    public void setCommitIndex(int commit_index) {
        this.commit_index = commit_index;
    }
}
