package com.exequiel.redditor.data;

import android.content.ContentResolver;
import android.net.Uri;


public class RedditContract {
    public static final String CONTENT_AUTHORITY = "com.exequiel.redditor";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SUBREDDITS = "subreddits";
    public static final String PATH_LINKS = "links";
    public static final String PATH_COMMENTS = "comments";


    interface SubRedditsColumns{
        String _ID = "_id";
        String SUBREDDIT_ORDER = "_order";
        String SUBREDDIT_ID = "id";
        String DISPLAY_NAME_PREFIXED = "display_name_prefixed";
        String DISPLAY_NAME = "display_name";
        String TITLE =  "title";
        String ICON_IMG = "icon_img";
        String OVER18 = "over18";
    }

    public static class SubReddits implements SubRedditsColumns{
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(PATH_SUBREDDITS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBREDDITS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBREDDITS;


        /** Matches: /subreddits/ */
        public static Uri buildDirUri() {
            return CONTENT_URI;
        }

        /** Matches: /subireddits/[_id]/ */
        public static final Uri buildUriWithRowId (long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }


        public static long getSubRedditUri(Uri subRedditUri) {
            return Long.parseLong(subRedditUri.getPathSegments().get(1));
        }

    }

    interface LinksColumns{
        String _ID = "_id";
        String LINK_ORDER = "_order";
        String LINK_ID = "id";
        String LINK_DOMAIN = "domain";
        String LINK_AUTHOR = "author";
        String LINK_SUBREDDIT = "subreddit";
        String LINK_SUBREDDIT_NAME_PREFIXED = "subreddit_name_prefixed";
        String LINK_TITLE = "title";
        String LINK_SCORE = "score";
        String LINK_SUBREDDIT_ID = "subreddit_id";
        String LINK_THUMBNAIL = "thumbnail";
        String LINK_PERMALINK = "permalink";
        String LINK_URL = "url";
        String LINK_CREATED = "created";
        String LINK_IS_VIDEO = "is_video";
        String LINK_NUM_COMMENTS = "num_comments";
        String LINK_IMAGE = "image";
//        String LINK_OVER18 = "over18";
    }

    public static class Links implements LinksColumns{
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(PATH_LINKS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBREDDITS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBREDDITS;


        /** Matches: /subreddits/ */
        public static Uri buildDirUri() {
            return CONTENT_URI;
        }

        /** Matches: /subireddits/[_id]/ */
        public static final Uri buildUriWithRowId (long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }


        public static long getLinkUri(Uri linksUri) {
            return Long.parseLong(linksUri.getPathSegments().get(1));
        }

    }


    interface CommentsColumns{
        String _ID = "_id";
        String COMMENTS_ID = "id";
        String COMMENTS_PARENT_ID = "comments_parent_id";
        String COMMENTS_SUBREDDIT_ID="subreddit_id";
        String COMMENTS_LINK_ID="link_id";
        String COMMENTS_AUTHOR="author";
        String COMMENTS_SCORE="score";
        String COMMENTS_BODY="body";
        String COMMENTS_CREATED="created";
    }

    public static class Comments implements CommentsColumns {
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(PATH_COMMENTS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENTS;


        /** Matches: /subreddits/ */
        public static Uri buildDirUri() {
            return CONTENT_URI;
        }

        /** Matches: /subireddits/[_id]/ */
        public static final Uri buildUriWithRowId (long rowId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(rowId)).build();
        }


        public static long getCommentUri(Uri commentsUri) {
            return Long.parseLong(commentsUri.getPathSegments().get(1));
        }

    }
}
