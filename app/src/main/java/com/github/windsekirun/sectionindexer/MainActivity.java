package com.github.windsekirun.sectionindexer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * SectionIndexer
 * Class: MainActivity
 * Created by winds on 2017-08-01.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinkedHashMap<String, Integer> mapIndex = new LinkedHashMap<>();
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> indexList = new ArrayList<>();
    String[] sections = new String[]{};

    LinearLayout sideIndex;
    IndexerListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sideIndex = findViewById(R.id.sideIndex);
        listView = findViewById(R.id.listView);

        init();
    }

    private void add(String... text) {
        Collections.addAll(list, text);
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("StringConcatenationInLoop")
    private void init() {
        add("가나다라", "가", "나", "다람쥐", "호랑이", "리스트", "마바", "바보", "사아", "아랑", "타타타타타타타타",
                "파파파파파파", "자차", "하하", "ABC", "BC", "C", "D", "F", "G", "I", "J", "K", "L",
                "사자", "개구리", "노랑이", "초록이", "하양이", "차", "자동차", "M", "N", "O", "P",
                "Q", "R", "S", "?", "!", "1", "2", "4");

        Collections.sort(list, OrderingByKorean.getComparator());

        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            String index = item.substring(0, 1);

            char c = index.charAt(0);
            if (OrderingByKorean.isKorean(c)) {
                index = String.valueOf(KoreanChar.getCompatChoseong(c));
            }

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
        indexList = new ArrayList<>(mapIndex.keySet());
        sections = new String[indexList.size()];
        indexList.toArray(sections);

        AlphabetAdapter adapter = new AlphabetAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        int selection = mapIndex.get(textView.getText().toString());
        listView.setSelection(selection);
    }

    public class AlphabetAdapter extends ArrayAdapter<String> implements SectionIndexer {
        LayoutInflater inflater;

        public AlphabetAdapter() {
            super(MainActivity.this, 0, list);
            inflater = LayoutInflater.from(MainActivity.this);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, parent, false);
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

        @Override
        public Object[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            String letter = sections[section];
            return mapIndex.get(letter);
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        class ViewHolder {
            TextView txtName;
        }
    }
}
