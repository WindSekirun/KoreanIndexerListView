package com.github.windsekirun.koreanindexer.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.windsekirun.koreanindexer.KoreanIndexerListView;
import com.github.windsekirun.koreanindexer.KoreanIndexerRecyclerView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * SectionIndexer
 * Class: MainActivity
 * Created by winds on 2017-08-01.
 */

public class RecyclerActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private KoreanIndexerRecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        setTitle("RecyclerView");

        recyclerView = findViewById(R.id.recyclerView);

        init();
    }

    private void add(String... text) {
        Collections.addAll(list, text);
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("StringConcatenationInLoop")
    private void init() {
        add("가나다라", "가", "나", "다람쥐", "호랑이", "리스트", "마바", "바보바보바보바보바보바보바보바보바보바보바보바보바보바보",
                "사아", "아랑", "타타타타타타타타", "스크롤스크롤스크롤",
                "파파파파파파", "자차", "하하", "ABC", "BC", "C", "D", "F", "G", "I", "J", "K", "L",
                "사자", "개구리", "노랑이", "초록이", "하양이", "차", "자동차", "M", "N", "O", "P",
                "Q", "R", "S", "?", "!", "1", "2", "4");
        add("가나다라", "4");

        AlphabetAdapter adapter = new AlphabetAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setKeywordList(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public class AlphabetAdapter extends KoreanIndexerRecyclerView.KoreanIndexerRecyclerAdapter<AlphabetAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtName.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView txtName;

            public ViewHolder(View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txtName);
            }
        }
    }
}
