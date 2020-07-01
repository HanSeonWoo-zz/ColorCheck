package com.SeonWoo.ColorCheck;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

import java.util.ArrayList;

public class PieChartAdapter extends RecyclerView.Adapter<PieChartAdapter.ViewHolder> {
    private ArrayList<PieData> mData;
    private Context mContext;
    private String pickedDate;


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        // 1. 컨텍스트 메뉴를 사용하려면 RecyclerView.ViewHolder를 상속받은 클래스에서
        // OnCreateContextMenuListener 리스너를 구현해야 합니다.

        protected TextView title;
        protected PieChart pieChart;


        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.piechart_tv_title);
            pieChart = itemView.findViewById(R.id.piechart);

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다.
            // ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.

            MenuItem Delete = menu.add(Menu.NONE, 1001, 1, "삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 4. 컨텍스트 메뉴에서 항목 클릭시 동작을 설정합니다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1002:
                        // 스크린 샷을 롱클릭에서 구현할 지 고민
                        break;
                }
                return true;
            }
        };

    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PieChartAdapter(Context context, ArrayList<PieData> list, String date) {
        mData = list;
        mContext = context;
        pickedDate = date;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public PieChartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.piechart_item, parent, false);
        PieChartAdapter.ViewHolder vh = new PieChartAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(PieChartAdapter.ViewHolder holder, int position) {
        String[] title = {"일일 통계", "주간 통계", "전체 통계"};
        holder.title.setText(title[position]);
        holder.pieChart.setData(mData.get(position));
        holder.pieChart.animateXY(1000, 1000);
        holder.pieChart.invalidate();

        holder.pieChart.getDescription().setEnabled(false);

        //        // PieChart
//        piechart.getDescription().setEnabled(false);
//        piechart.setExtraOffsets(5, 10, 5, 5);
//        piechart.setDragDecelerationFrictionCoef(0.95f);
//        piechart.setDrawHoleEnabled(false);
//        piechart.setHoleColor(android.graphics.Color.WHITE);
//        piechart.setTransparentCircleRadius(61f);


//        // 오른쪽 밑 부분에 넣을 제목 역할
//        Description description_pie = new Description();
//
//        //닉네임 없을 때 안 보이게
//        if (pref.getString("Nickname", "").contentEquals("")) {
//            description_pie.setText("");
//        }
//
//        // 닉네임 설정
//        else {
//            description_pie.setText(pref.getString("Nickname", ""));
//        }
//        description_pie.setTextSize(15);
//        piechart.setDescription(description_pie);
//
//        // 처음 그려질 때, 1초에 걸쳐 차르르 하고 펼쳐지는 효과
//        piechart.animateY(1000, Easing.EaseInOutCubic); //애니메이션

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return (null != mData ? mData.size() : 0);
    }

}