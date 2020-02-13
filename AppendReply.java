public class AppendReply extends Message {

    private int commit_index;

    @Override
    public String getType() {
        return "AppendReply";
    }

    public int getCommitIndex() {
        return commit_index;
    }

    public void setCommitIndex(int commit_index) {
        this.commit_index = commit_index;
    }
}
