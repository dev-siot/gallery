package com.siot.sss.hsgallery.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.siot.sss.hsgallery.R;
import com.siot.sss.hsgallery.app.activity.MainActivity;
import com.siot.sss.hsgallery.app.recycler.adapter.LogAdapter;
import com.siot.sss.hsgallery.app.model.UseLog;
import com.siot.sss.hsgallery.util.data.db.table.DBOpenHelper;
import com.siot.sss.hsgallery.util.data.db.table.Tables;
import com.siot.sss.hsgallery.util.view.recyclerview.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by SSS on 2015-08-04.
 */
public class LogFragment extends RecyclerViewFragment<LogAdapter, UseLog> implements View.OnClickListener {
    @InjectView(R.id.log_recycler) protected RecyclerView gallery;
    @InjectView(R.id.log_all) protected TextView btnAll;
    @InjectView(R.id.log_create) protected TextView btnCreate;
    @InjectView(R.id.log_read) protected TextView btnRead;
    @InjectView(R.id.log_update) protected TextView btnUpdate;
    @InjectView(R.id.log_delete) protected TextView btnDelete;
    private CompositeSubscription subscription;
    private Toolbar toolbar;

    private List<UseLog> useLogs;

    private enum State{
        ALL, SAVE, READ, UPDATE, DELETE
    }
    private State state;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.inject(this, view);
        this.toolbar = ((MainActivity)this.getActivity()).getToolbar();
        this.toolbar.setTitle(R.string.log);
        this.setupRecyclerView(this.gallery);
        this.state = State.ALL;
        this.useLogs = new ArrayList<>();
        btnAll.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.subscription != null && !this.subscription.isUnsubscribed()) this.subscription.unsubscribe();
        ButterKnife.reset(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        DBOpenHelper helper = new DBOpenHelper(this.getActivity().getBaseContext());
        this.items.clear();
        helper.open();
        Cursor cursor = helper.getAllColumnsUseLog();
        if(cursor.moveToFirst()) {
            do {
                UseLog useLog = new UseLog(
                    cursor.getInt(cursor.getColumnIndex(Tables.UseLog._ID)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.DATE)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.NAME)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.PICTUREID)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.TYPE)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.BUCKET)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.BUCKETNAME)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.DATA)),
                    cursor.getString(cursor.getColumnIndex(Tables.UseLog.TITLE)),
                    cursor.getInt(cursor.getColumnIndex(Tables.UseLog.WIDTH)),
                    cursor.getInt(cursor.getColumnIndex(Tables.UseLog.HEIGHT))
                );
                this.useLogs.add(useLog);
            } while (cursor.moveToNext());
        }
        this.items.addAll(useLogs);

        helper.close();
    }

    private List<UseLog> getUseLogByType(UseLog.Type type){
        List<UseLog> useLogList = new ArrayList<>();
        if(type != null) {
            for (UseLog log : this.useLogs) {
                if (log.type.equals(UseLog.getTypeString(type))) {
                    useLogList.add(log);
                }
            }
        }else{
            useLogList.addAll(useLogs);
        }
        return useLogList;
    }

    private void toolbarMenuItemChange(State state){
        int id = 12;
        switch (state){
            case ALL:
                this.toolbar.getMenu().findItem(id).setVisible(true);
                break;
            case SAVE:
                this.toolbar.getMenu().findItem(id).setVisible(false);
                break;
            case DELETE:
                break;
            case READ:
                break;
            case UPDATE:
                break;
        }
    }

    @Override
    protected LogAdapter getAdapter() {
        return new LogAdapter(this.items, this);
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new LinearLayoutManager(this.getActivity());
    }

    @Override
    public void onRecyclerViewItemClick(View view, int position) {
    }

    public void notifyDataSetChanged(List<UseLog> useLog){
        items.clear();
        items.addAll(useLog);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnAll.getId()){
            notifyDataSetChanged(this.getUseLogByType(null));
        }else if (v.getId() == btnCreate.getId()){
            notifyDataSetChanged(this.getUseLogByType(UseLog.Type.SAVE));
        }else if (v.getId() == btnRead.getId()){
            notifyDataSetChanged(this.getUseLogByType(UseLog.Type.READ));
        }else if(v.getId() == btnUpdate.getId()){
            notifyDataSetChanged(this.getUseLogByType(UseLog.Type.UPDATE));
        }else if (v.getId() == btnDelete.getId()){
            notifyDataSetChanged(this.getUseLogByType(UseLog.Type.DELETE));
        }
    }
}
