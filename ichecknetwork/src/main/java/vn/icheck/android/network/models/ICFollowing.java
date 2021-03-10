package vn.icheck.android.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ICFollowing {

    @Expose
    @SerializedName("rows")
    private List<Rows> rows;
    @Expose
    @SerializedName("count")
    private int count;

    public ICFollowing() {
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static class Rows{
        @Expose
        @SerializedName("object")
        private RowObject object;
        @Expose
        @SerializedName("object_type")
        private String objectType;
        @Expose
        @SerializedName("object_id")
        private long objectId;
        @Expose
        @SerializedName("created_at")
        private String createdAt;
        @Expose
        @SerializedName("id")
        private long id;

        public RowObject getRowObject() {
            return object;
        }

        public void setRowObject(RowObject object) {
            this.object = object;
        }

        public String getObjectType() {
            return objectType;
        }

        public void setObjectType(String objectType) {
            this.objectType = objectType;
        }

        public long getObjectId() {
            return objectId;
        }

        public void setObjectId(long objectId) {
            this.objectId = objectId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class RowObject {
        @Expose
        @SerializedName("following_count")
        private int followingCount;
        @Expose
        @SerializedName("follower_count")
        private int followerCount;
        @Expose
        @SerializedName("cover_thumbnails")
        private CoverThumbnails coverThumbnails;
        @Expose
        @SerializedName("avatar_thumbnails")
        private AvatarThumbnails avatarThumbnails;
        @Expose
        @SerializedName("last_name")
        private String lastName;
        @Expose
        @SerializedName("first_name")
        private String firstName;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("id")
        private long id;

        public int getFollowingCount() {
            return followingCount;
        }

        public void setFollowingCount(int followingCount) {
            this.followingCount = followingCount;
        }

        public int getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(int followerCount) {
            this.followerCount = followerCount;
        }

        public CoverThumbnails getCoverThumbnails() {
            return coverThumbnails;
        }

        public void setCoverThumbnails(CoverThumbnails coverThumbnails) {
            this.coverThumbnails = coverThumbnails;
        }

        public AvatarThumbnails getAvatarThumbnails() {
            return avatarThumbnails;
        }

        public void setAvatarThumbnails(AvatarThumbnails avatarThumbnails) {
            this.avatarThumbnails = avatarThumbnails;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class CoverThumbnails {
        @SerializedName("original")
        @Expose
        private String original;

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

    public static class AvatarThumbnails {
        @SerializedName("original")
        @Expose
        private String original;

        public String small;

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }
}
