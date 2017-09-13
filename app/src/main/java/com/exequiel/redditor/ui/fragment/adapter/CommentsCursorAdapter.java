package com.exequiel.redditor.ui.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.exequiel.redditor.R;
import com.exequiel.redditor.data.CommentsLoader;
import com.exequiel.redditor.data.RedditContract;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by m4ch1n3 on 28/8/2017.
 */

public class CommentsCursorAdapter extends CursorTreeAdapter {
    @BindView(R.id.textViewUserName) TextView userName;
    @BindView(R.id.textViewCommentScore)TextView commentScore;
    @BindView(R.id.textViewBody) TextView body;

    private final Context context;

    public CommentsCursorAdapter(Cursor cursor, Context context) {
        super(cursor, context);
        this.context = context;
    }

    /**
     * Since i will use the same view for the children and the parent
     * @param context
     * @param viewGroup
     * @return
     */
    private View internalnewView(Context context, ViewGroup viewGroup){
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, viewGroup, false);
        view.setPadding(0, 0, 0, 20);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Since i will use the same view for the children and the parent
     * @param view
     * @param context
     * @param cursor
     */
    private void internalBindGroupView(View view, Context context, Cursor cursor){
        userName.setText(cursor.getString(CommentsLoader.Query.COMMENTS_AUTHOR));
        commentScore.setText(cursor.getString(CommentsLoader.Query.COMMENTS_SCORE));
        body.setText(cursor.getString(CommentsLoader.Query.COMMENTS_BODY));
    }

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {
        String parentId = cursor.getString(cursor.getColumnIndex(RedditContract.Comments.COMMENTS_ID));
        Cursor childCursor = context.getContentResolver().query(RedditContract.Comments.CONTENT_URI, CommentsLoader.Query.PROJECTION, RedditContract.Comments.COMMENTS_PARENT_ID + " = \"" + parentId+"\"", null, null);
        return childCursor;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        return internalnewView(context, viewGroup);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
        internalBindGroupView(view, context, cursor);
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        return internalnewView(context, viewGroup);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
        internalBindGroupView(view, context, cursor);
    }


}
