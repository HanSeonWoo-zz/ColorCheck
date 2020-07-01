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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class BarChartAdapter extends RecyclerView.Adapter<BarChartAdapter.ViewHolder> {
    private ArrayList<BarData> mData;
    private Context mContext;
    private String pickedDate;


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
    public BarChartAdapter(Context context, ArrayList<BarData> list, String date) {
        mData = list;
        mContext = context;
        pickedDate = date;
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
        String[] title = {"PINK", "ORAGNE", "GREEN", "BLUE", "PURPLE"};
        holder.title.setText(title[position]);
        holder.barChart.setData(mData.get(position));
        holder.barChart.animateXY(1000, 1000);
        holder.barChart.invalidate();

        holder.barChart.getDescription().setEnabled(false);

        XAxis xAxis = holder.barChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);


        YAxis leftAxis = holder.barChart.getAxisLeft();
        // 최대 최소 지정
        leftAxis.setAxisMaximum(8f);
        leftAxis.setAxisMinimum(0f);

        // 줄 사이 거리 지정
        leftAxis.setYOffset(2f);

        leftAxis.setDrawAxisLine(false);
        leftAxis.setLabelCount(5, false);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);

        // 점선으로 대쉬라인을 그음
        leftAxis.enableGridDashedLine(3, 3, 0);

        // 우측 Y축은 사용하지 않음.
        holder.barChart.getAxisRight().setEnabled(false);

        // 선택한 날의 요일 확인해서 요일 셋팅
        final HashMap<Integer, String> numMap = new HashMap<>();
        if (Character.toString(pickedDate.charAt(14)).contentEquals("월")) {
            numMap.put(0, "월");
            numMap.put(1, "화");
            numMap.put(2, "수");
            numMap.put(3, "목");
            numMap.put(4, "금");
            numMap.put(5, "토");
            numMap.put(6, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("화")) {
            numMap.put(6, "월");
            numMap.put(0, "화");
            numMap.put(1, "수");
            numMap.put(2, "목");
            numMap.put(3, "금");
            numMap.put(4, "토");
            numMap.put(5, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("수")) {
            numMap.put(5, "월");
            numMap.put(6, "화");
            numMap.put(0, "수");
            numMap.put(1, "목");
            numMap.put(2, "금");
            numMap.put(3, "토");
            numMap.put(4, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("목")) {
            numMap.put(4, "월");
            numMap.put(5, "화");
            numMap.put(6, "수");
            numMap.put(0, "목");
            numMap.put(1, "금");
            numMap.put(2, "토");
            numMap.put(3, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("금")) {
            numMap.put(3, "월");
            numMap.put(4, "화");
            numMap.put(5, "수");
            numMap.put(6, "목");
            numMap.put(0, "금");
            numMap.put(1, "토");
            numMap.put(2, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("토")) {
            numMap.put(2, "월");
            numMap.put(3, "화");
            numMap.put(4, "수");
            numMap.put(5, "목");
            numMap.put(6, "금");
            numMap.put(0, "토");
            numMap.put(1, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("일")) {
            numMap.put(1, "월");
            numMap.put(2, "화");
            numMap.put(3, "수");
            numMap.put(4, "목");
            numMap.put(5, "금");
            numMap.put(6, "토");
            numMap.put(0, "일");
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return numMap.get((int) value);
            }
        });

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return (null != mData ? mData.size() : 0);
    }

}

