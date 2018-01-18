package app.rsb.study.realtimesearch;

public class ListData {
    /**
     * 리스트 정보를 담고 있을 객체 생성
     */

    //URL
    private String mUrl;

    //순위
    private String mRank;

    // 검색어
    private String mTitle;

    // 순위 Up/Down
    private String mUpdown;

    private Boolean mIsUp;

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setRank(String rank) {
        mRank = rank;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setUpdown(String updown) {
        mUpdown = updown;
    }

    public void setIsUp(Boolean isUp) {
        mIsUp = isUp;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public String getRank() {
        return this.mRank;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getUpdown() {
        return this.mUpdown;
    }

    public Boolean getIsUp() {
        return this.mIsUp;
    }
}
