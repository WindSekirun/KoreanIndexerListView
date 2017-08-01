package com.github.windsekirun.koreanindexer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * SectionIndexer
 * Class: MainActivity
 * Created by winds on 2017-08-01.
 */

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private KoreanIndexerListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

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

        AlphabetAdapter adapter = new AlphabetAdapter();
        listView.setKeywordList(list);
        listView.setAdapter(adapter);
    }

    public class AlphabetAdapter extends KoreanIndexerListView.KoreanIndexerAdapter<String> {

        AlphabetAdapter() {
            super(MainActivity.this, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.txtName = convertView.findViewById(R.id.txtName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtName.setText(list.get(position));

            return convertView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        class ViewHolder {
            TextView txtName;
        }
    }
}
