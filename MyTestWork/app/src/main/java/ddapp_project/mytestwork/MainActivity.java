package ddapp_project.mytestwork;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import java.util.concurrent.TimeUnit;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import ddapp_project.mytestwork.api.ApiEndpointInterface;
import ddapp_project.mytestwork.base.DB;
import ddapp_project.mytestwork.object.Courses;
import ddapp_project.mytestwork.object.Student;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends ListActivity implements OnScrollListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private String mFirstName;
    private String mLastName;
    private String mId;
    private String mBirthday;
    private float mCourse_0;
    private float mCourse_1;
    private float mCourse_2;
    private float mCourse_3;
    private float mGpa;

    private ApiEndpointInterface apiService;

    DB mDb;
    Cursor mCursor;

    ArrayList<ArrayList<String>> forData = new ArrayList<ArrayList<String>>();
    ArrayList<String> forDataElement;
    private ListView mList;
    private StringAdapter mAdapter;
    private View mFooter;
    private LoadMoreAsyncTask mLoadingTask = new LoadMoreAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forDataElement = new ArrayList<String>();
        mDb = new DB(this);
        mDb.open();
        mCursor = mDb.getAllData();
        startManagingCursor(mCursor);
        initRetrofit();
        doRequest();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                generate();
            }
        });
        thread.start();


        mAdapter = new StringAdapter(this);
        mFooter = getLayoutInflater().inflate(R.layout.footer_loading, null);
        mList = getListView();
        mList.addFooterView(mFooter); // it's important to call 'addFooter' before 'setAdapter'
        mList.setAdapter(mAdapter);
        mList.setOnScrollListener(this);
        mLoadingTask.execute(0);
    }

    /**
     * Метод который генерит всю ету муть
     */
    public void generate() {
        forDataElement.clear();
        mCursor = mDb.getAllData();
        String firstName;
        String lastName;
        String birthday;
        if(mCursor!=null&&mCursor. moveToFirst()) {
            do {
                firstName = mCursor.getString(mCursor.getColumnIndexOrThrow(DB.COLUMN_STUDENT_FIRST_NAME));
                forDataElement.add(firstName);
                lastName = mCursor.getString(mCursor.getColumnIndexOrThrow(DB.COLUMN_STUDENT_LAST_NAME));
                forDataElement.add(lastName);
                birthday = mCursor.getString(mCursor.getColumnIndexOrThrow(DB.COLUMN_BIRTHDAY));
                forDataElement.add(birthday);
                forData.add(forDataElement);
            }while(mCursor.moveToNext());

        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
        boolean loadMore = firstVisible + visibleCount >= totalCount;

        if (loadMore && mLoadingTask.getStatus() == AsyncTask.Status.FINISHED) {
            DB.limit+=20;
            generate();
            mLoadingTask = new LoadMoreAsyncTask();
            mLoadingTask.execute(totalCount);
        }
    }

    private class LoadMoreAsyncTask extends AsyncTask<Integer, Void, Collection<ArrayList<String>>> {
        @SuppressWarnings("unchecked")
        @Override
        protected Collection<ArrayList<String>> doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
                Collection<ArrayList<String>> data = forData;
                return data;
            } catch (Exception e) {
                Log.e(TAG, "Loading data", e);
            }
            return Collections.EMPTY_LIST;
        }

        @Override
        protected void onPostExecute(Collection<ArrayList<String>> data) {
            if (data.isEmpty()) {
                Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }

            mAdapter.add(forData);
            mAdapter.notifyDataSetChanged();
            int index = mList.getFirstVisiblePosition();
            int top = (mList.getChildAt(0) == null) ? 0 : mList.getChildAt(0).getTop();
            mList.setSelectionFromTop(index, top);
        }
    }

    private void doRequest() {

        final Call<List<Student>> call = apiService.getStudents();
        call.enqueue(new Callback<List<Student>>() {

            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                /**
                 * Вот твой список студентов :
                 * response.body()
                 * :)
                 */

                for (int i = 0; i < response.body().size(); i++) {

                    mFirstName = response.body().get(i).getFirstName();

                    mLastName = response.body().get(i).getLastName();

                    mBirthday = response.body().get(i).getBirthday();

                    mId = response.body().get(i).getId();
                    Courses[] curs = response.body().get(i).getCourses();
                    for (int j = 0; j < curs.length; j++) {
                        if (j == 0) {
                            mCourse_0 = Float.parseFloat(curs[j].getMark());
                        } else if (j == 1) {
                            mCourse_1 = Float.parseFloat(curs[j].getMark());
                        } else if (j == 2) {
                            mCourse_2 = Float.parseFloat(curs[j].getMark());
                        } else if (j == 3) {
                            mCourse_3 = Float.parseFloat(curs[j].getMark());
                            mGpa = (mCourse_0 + mCourse_1 + mCourse_2 + mCourse_3) / 4;
                        }
                    }

                    mCursor = mDb.addRec(mFirstName, mLastName, mId, mBirthday, (int) mCourse_0, (int) mCourse_1, (int) mCourse_2, (int) mCourse_3, mGpa);
                    Log.d(TAG, "onResponse: response.isSuccessful() " + response.isSuccessful());
                    Log.d(TAG, "onResponse: response.size() = " + response.body().size());
                    Log.d(TAG, "onResponse: getFirstName " + response.body().get(i).getFirstName());
                    Log.d(TAG, "onResponse: getLastName " + response.body().get(i).getLastName());
                    Log.d(TAG, "onResponse: getBirthday " + response.body().get(i).getBirthday());
                    Log.d(TAG, "onResponse: getID " + response.body().get(i).getId());
                }

            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private void initRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ddapp-sfa-api-dev.azurewebsites.net/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(ApiEndpointInterface.class);
    }

    public void onDestroy(){
        super.onDestroy();
    }

}
