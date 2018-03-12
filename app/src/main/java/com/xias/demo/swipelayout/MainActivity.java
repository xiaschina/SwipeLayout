package com.xias.demo.swipelayout;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xias.demo.swipelayout.util.StatusBarCompat;
import com.xias.demo.swipelayout.view.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.compat(this,getResources().getColor(R.color.colorPrimary));
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        for(int i=0 ;i < 20; i++)
            mList.add("第" + i);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter());
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclewViewHolder>{
        @NonNull
        @Override
        public RecyclewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent, false);
            return new RecyclewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclewViewHolder holder, int position) {
            if(position % 2 == 0)
                holder.mSwipe.setCanSwipe(false);
            holder.mSwipe.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onSwipeOpen() {
                    Toast.makeText(MainActivity.this, "OPEN", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeClose() {
                    Toast.makeText(MainActivity.this, "CLOSE", Toast.LENGTH_SHORT).show();
                }
            });
            holder.mText.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class RecyclewViewHolder extends RecyclerView.ViewHolder{
            public TextView mText;
            public SwipeLayout mSwipe;

            public RecyclewViewHolder(View itemView) {
                super(itemView);
                this.mSwipe = itemView.findViewById(R.id.swipe);
                this.mText = itemView.findViewById(R.id.text);
            }
        }
    }

}
