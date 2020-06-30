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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;

import java.util.ArrayList;

public class BarChartAdapter extends RecyclerView.Adapter<BarChartAdapter.ViewHolder> {

    private ArrayList<BarData> mData;
    private Context mContext;


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        // 1. 컨텍스트 메뉴를 사용하려면 RecyclerView.ViewHolder를 상속받은 클래스에서
        // OnCreateContextMenuListener 리스너를 구현해야 합니다.

        protected TextView title;
        protected BarChart barChart;


        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.item_tv_title);
            barChart = itemView.findViewById(R.id.barchart);

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
    public BarChartAdapter(Context context, ArrayList<BarData> list) {
        mData = list;
        mContext = context;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public BarChartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.barchart_item, parent, false);
        BarChartAdapter.ViewHolder vh = new BarChartAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(BarChartAdapter.ViewHolder holder, int position) {
        String[] title = {"PINK", "ORAGNE" ,"GREEN","BLUE","PURPLE"};

        holder.title.setText(title[position]);
        holder.barChart.setData(mData.get(position));
        holder.barChart.animateXY(1000,1000);
        holder.barChart.invalidate();

        holder.barChart.getDescription().setEnabled(false);

        XAxis xAxis = holder.barChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);


        YAxis leftAxis = holder.barChart.getAxisLeft();
        leftAxis.setAxisMaximum(8f);
        leftAxis.setYOffset(2f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setLabelCount(5, true);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.enableGridDashedLine(3,3,0);

        YAxis rightAxis = holder.barChart.getAxisRight();

        // 오른쪽 Y축의 숫자를 없앰.
//        rightAxis.setDrawLabels(false);
//        // 오른쪽 막는 선을 없앰.
//        rightAxis.setDrawAxisLine(false);
//        // 차트의 중간에 그어지는 선을 없앰.
//        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return (null != mData ? mData.size() : 0);
    }

}

