package app.rsb.study.realtimesearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView = null;
    private ListView mListView2 = null;
    private ListViewAdapter naverAdapter = null;
    private ListViewAdapter daumAdapter = null;
    ProgressDialog mDialog = null;
    public static KeywordTask asyncTask;
    ConnectivityManager cManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setElevation(0);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                View include01 = findViewById(R.id.include1);
                View include02 = findViewById(R.id.include2);
                ImageView img = (ImageView) findViewById(R.id.imageView2);

                if (position == 0) {
                    include01.setVisibility(View.VISIBLE);
                    include02.setVisibility(View.INVISIBLE);
                    img.setImageResource(R.drawable.naver);
                }

                if (position == 1) {
                    include01.setVisibility(View.INVISIBLE);
                    include02.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.daum);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(isInternetCon() == false) {
            Toast.makeText(this, "인터넷에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            asyncTask = new KeywordTask();
            asyncTask.execute();
        }

        mListView = (ListView) findViewById(R.id.listView);
        mListView2 = (ListView) findViewById(R.id.listView2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mListView.setNestedScrollingEnabled(true);
            mListView2.setNestedScrollingEnabled(true);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(naverAdapter.mListData.get(i).getUrl()));
                startActivity(intent);
            }
        });

        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(daumAdapter.mListData.get(i).getUrl()));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            if(isInternetCon() == false) {
                Toast.makeText(this, "인터넷에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                asyncTask = new KeywordTask();
                asyncTask.execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public class KeywordTask extends AsyncTask<String,Void, String> {
        public String result;

        @Override
        protected void onPreExecute() {

            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("가져오는 중..");
            mDialog.setCancelable(false);
            mDialog.show();

            naverAdapter = new ListViewAdapter();
            daumAdapter = new ListViewAdapter();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Document naver = Jsoup.connect("http://www.naver.com")
                        .timeout(5000)
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                        .get();

                Elements sel = naver.select("div.ah_list.PM_CL_realtimeKeyword_list_base > ul.ah_l").get(0).select("li.ah_item > a.ah_a");

                for(Element e : sel) {

                    String nUrl = e.attr("href");
                    String rank = e.select("span.ah_r").text()+".";
                    String title = e.select("span.ah_k").text();
                    String updown = "NEW";
                    naverAdapter.addItem(nUrl, rank, title, updown, false);
                }

                Document daum = Jsoup.connect("http://m.daum.net")
                        .timeout(5000)
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,tr;q=0.2")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .get();

                Elements sel2 = daum.select("div.keyword_issue > ol[class=list_issue #hotissue list_realtime] > li");

                for(Element e : sel2) {

                    String dUrl = e.select("a.link_issue").attr("href");
                    String rankD = e.select("a.link_issue > em.num_issue").text()+".";
                    String titleD = e.select("a.link_issue > span.txt_issue").text();
                    String updownD = e.select("em.state_issue > span.num_rank").text();
                    String up = e.select("em.state_issue > span.ico_mtop.ico_up").text();
                    if(updownD == "") {
                        updownD = "NEW";
                    }
                    Boolean isup;

                    if(up != "") {
                        isup = true;
                    } else {
                        isup = false;
                    }

                    daumAdapter.addItem(dUrl, rankD, titleD, updownD, isup);
                }

                Document nate = Jsoup.connect("https://m.search.daum.net/nate?q=dd&w=tot&thr=mnms&input_search=dd")
                        .timeout(3000)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,tr;q=0.2")
                        .referrer("http://m.nate.com/")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .get();

                System.out.println(nate);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            mDialog.dismiss();
            super.onPostExecute(s);
            naverAdapter.notifyDataSetChanged();
            daumAdapter.notifyDataSetChanged();
            mListView.setAdapter(naverAdapter);
            mListView2.setAdapter(daumAdapter);
        }
    }

        private class ListViewAdapter extends BaseAdapter {
            private Context mContext = null;
            private ArrayList<ListData> mListData = new ArrayList<ListData>();

            public ListViewAdapter() {

            }

            @Override
            public int getCount() {
                return mListData.size();
            }

            @Override
            public Object getItem(int position) {
                return mListData.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            public void addItem(String url, String rank, String title, String updown, Boolean isUp){
                ListData mData = new ListData();

                mData.setUrl(url);
                mData.setRank(rank);
                mData.setTitle(title);
                mData.setUpdown(updown);
                mData.setIsUp(isUp);
                mListData.add(mData);
            }

            public void remove(int position){
                mListData.remove(position);
                dataChange();
            }

            public void dataChange(){
                naverAdapter.notifyDataSetChanged();
                daumAdapter.notifyDataSetChanged();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int pos = position;
                final Context context = parent.getContext();

                // "listview_item" Layout을 inflate하여 convertView 참조 획득.
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.itemstyle, parent, false);
                }

                // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
                TextView rank = (TextView) convertView.findViewById(R.id.rank);
                TextView title = (TextView) convertView.findViewById(R.id.title);
                ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
                TextView updown = (TextView) convertView.findViewById(R.id.updown);

                // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
                ListData listViewItem = mListData.get(position);

                // 아이템 내 각 위젯에 데이터 반영
                rank.setText(listViewItem.getRank());
                title.setText(listViewItem.getTitle());
                updown.setText(listViewItem.getUpdown());

                if(listViewItem.getUpdown() == "NEW") {
                    updown.setTextColor(Color.RED);
                } else {
                    updown.setTextColor(Color.BLACK);
                }

                if(listViewItem.getIsUp() == true) {
                    arrow.setVisibility(View.VISIBLE);
                } else {
                    arrow.setVisibility(View.INVISIBLE);
                }

                return convertView;
            }
        }

    private boolean isInternetCon() {
        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cManager.getActiveNetworkInfo() != null;
    }
    }
