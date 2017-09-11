package eth.fingerfixer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DownloadActivity extends AppCompatActivity {
    FTP_Thread ft;
    boolean state = FALSE;
    private String current_path = null;
    private String[] titleList = null;
    private String selectedNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ft = new FTP_Thread();

        current_path = ft.FTP_get_path();

        try {
            titleList = ft.FTP_get_file_list(current_path);

            if (titleList.length != 0) {
                state = TRUE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (state) {
            try {
                ListView listview;
                ListViewAdapter adapter;

                // Adapter 생성
                adapter = new ListViewAdapter();

                // 리스트뷰 참조 및 Adapter달기
                listview = (ListView) findViewById(R.id.downlistview);
                listview.setAdapter(adapter);

                // 아이템 추가
                for (int i = 0; i < titleList.length; i++) {
                    adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_level1), titleList[i], "ETH");
                }

                // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        // get item
                        ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                        String titleStr = item.getTitle();
                        Drawable iconDrawable = item.getLevel();

                        // TODO : use item data.
                        selectedNote = titleStr;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ImageView imgView;

            // 파일이 없을때 이미지 뷰 안보이게
            imgView = (ImageView) findViewById(R.id.imgView);
            imgView.setImageResource(R.drawable.no_result);
        }
    }

    public void fabDownloadOnClick(View v) {
        if (selectedNote == null)
            Toast.makeText(this, "내려받을 악보를 선택해 주세요.", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, selectedNote + "를 내려받습니다.", Toast.LENGTH_SHORT).show();

            ft.FTP_download(this, selectedNote);
        }
    }
}