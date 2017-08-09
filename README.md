# KoreanIndexerListView
[![](https://jitpack.io/v/WindSekirun/KoreanIndexerListView.svg)](https://jitpack.io/#WindSekirun/KoreanIndexerListView)

![](https://i0.wp.com/blog.uzuki.live/wp-content/uploads/2017/08/pjimage.jpg?resize=768%2C768&ssl=1)

본 라이브러리는 한글이 사용되는 데이터 환경에서 SectionIndexer를 좀 더 쉽게 사용할 수 있게 하는 리스트뷰, 리사이클뷰 위젯으로, 아래의 기능을 포함합니다.

* SectionIndexer
* 알파벳 인덱스 뷰
* 한글 중심 정렬, 초성 인덱스
    * 현재 정렬 우선순위는 아래와 같습니다.
    * 한글 (초성)
    * 영문
    * 숫자
    * 특수문자
* RecyclerView에 대한 지원
    
본 코드에 대한 설명은 개인 블로그인 PyxisPub에서 [안드로이드 ListView + SectionIndexer](https://blog.uzuki.live/android-custom-listview-sectionindexr/) 포스트로 보실 수 있습니다.

## 사용법

### [샘플 모듈](https://github.com/WindSekirun/KoreanIndexerListView/tree/master/sample)

###  의존 라이브러리로 추가
*rootProject/build.gradle*
```	
allprojects {
    repositories {
	    maven { url 'https://jitpack.io' }
    }
}
```

*app/build.gradle*
```
dependencies {
    compile 'com.github.WindSekirun:KoreanIndexerListView:1.1.0'
}
```

### XML에 위젯 추가
````XML
<com.github.windsekirun.koreanindexer.KoreanIndexerListView
        android:id="@+id/listView"
        android:layout_width="match_parent"
        android:clipChildren="false"
        android:layout_height="match_parent"
        android:dividerHeight="0px"
        android:divider="@android:color/transparent"
        <!-- 아래부터 필수값이 아님 -->
        app:indexerBackground="#ffffff" 
        app:indexerRadius="60"
        app:indexerTextColor="#000000"
        app:indexerWidth="20"
        app:sectionBackground="#ffffff"
        app:sectionDelay="1000"
        app:sectionTextColor="#000000"
        app:indexerMargin="0"
        app:useSection="true" />
````

````XML
<com.github.windsekirun.koreanindexer.KoreanIndexerRecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:clipChildren="false"
        android:layout_height="match_parent"
        android:dividerHeight="0px"
        android:divider="@android:color/transparent"
        <!-- 아래부터 필수값이 아님 -->
        app:indexerBackground="#ffffff"
        app:indexerRadius="60"
        app:indexerTextColor="#000000"
        app:indexerWidth="20"
        app:sectionBackground="#ffffff"
        app:sectionDelay="1000"
        app:sectionTextColor="#000000"
        app:indexerMargin="0"
        app:useSection="true" />
````

#### 필드 설명
각 XML 필드에 대한 설명은 [여기](https://github.com/WindSekirun/KoreanIndexerListView/blob/master/library/src/main/java/com/github/windsekirun/koreanindexer/KoreanIndexerListView.java#L95) 에서 보실 수 있습니다. 

### 액티비티에 구현
SectionIndexer 기능을 활성화하기 위하여 SectionIndexer 의 구현이 필요하나 이 라이브러리는 KoreanIndexerListView.KoreanIndexerAdapter<T> 로 대신할 수 있습니다.

````Java
AlphabetAdapter adapter = new AlphabetAdapter();
listView.setKeywordList(list);
listView.setAdapter(adapter);

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
````

````Java
AlphabetAdapter adapter = new AlphabetAdapter();
LinearLayoutManager layoutManager = new LinearLayoutManager(this);
recyclerView.setKeywordList(list);
recyclerView.setLayoutManager(layoutManager);
recyclerView.setAdapter(adapter);

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
````

#### 주의

* 동적으로 아이템을 추가할 경우에 대한 작동을 보증하지 않습니다.

## 가져온 제 3자 코드
본 리스트뷰 위젯 라이브러리는 빠른 개발을 위하여 아래의 코드를 수정하여 private static class로서 포함하였습니다.

* [KoreanTextMatcher](https://github.com/bangjunyoung/KoreanTextMatcher)
    * 한글에서 초성을 추출하기 위한 용도
    * KoreanChar 단일 파일 사용
    * getCompatChoseong 및 필요한 메소드, 필드 제외 후 전부 삭제
* [OrderingByKoreanEnglishNumberSpecial](http://reimaginer.tistory.com/entry/한글영어특수문자-순-정렬하는-java-compare-메서드-만들기)
    * 정렬 순서를 한글 > 영어 > 숫자 > 특수문자 순으로 정렬하기 위한 용도
    * CharUtils, OrderingByKoreanEnglishNumberSpecial 통합
    * OrderingByKoreanEnglishNumberSpecial 클래스의 이름을 OrderingByKorean으로 변경

## 라이센스 
```
Copyright 2017 WindSekirun (DongGil, Seo)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 업데이트 내역
* 1.1.0 (2017. 08. 10.)
	* 이슈 #1 #2 #3 해결
	* 리사이클 뷰 지원 시작 (LinearLayoutManager, GridLayoutManager)
	* 인덱서 텍스트의 상 / 하 마진 설정 값 추가
	* 계산식 일부 보정
	* Generic 정식 지원
		* 단, setKeywordList 는 원할한 정렬을 위하여 String 형태로만 받습니다.
		* setKeywordList 의 리스트와 Adapter에 실제 사용되는 리스트는 달라도 됩니다.
* 1.0.0 (2017. 08. 02.) 첫 릴리즈
