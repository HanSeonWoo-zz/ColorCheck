package com.SeonWoo.ColorCheck;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Color> mData;
    private Context mContext;
    private int[] Colorlist;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        // 1. 컨텍스트 메뉴를 사용하려면 RecyclerView.ViewHolder를 상속받은 클래스에서
        // OnCreateContextMenuListener 리스너를 구현해야 합니다.

        protected TextView date;
        protected TextView pink;
        protected TextView orange;
        protected TextView green;
        protected TextView blue;
        protected TextView purple;

        protected Button box_pink;
        protected Button box_orange;
        protected Button box_green;
        protected Button box_blue;
        protected Button box_purple;


        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            date = itemView.findViewById(R.id.recycler_tv_date);
            pink = itemView.findViewById(R.id.tv_pink);
            orange = itemView.findViewById(R.id.tv_orange);
            green = itemView.findViewById(R.id.tv_green);
            blue = itemView.findViewById(R.id.tv_blue);
            purple = itemView.findViewById(R.id.tv_purple);

            box_pink = itemView.findViewById(R.id.item_box_pink);
            box_orange = itemView.findViewById(R.id.item_box_orange);
            box_green = itemView.findViewById(R.id.item_box_green);
            box_blue = itemView.findViewById(R.id.item_box_blue);
            box_purple = itemView.findViewById(R.id.item_box_purple);

            itemView.setOnCreateContextMenuListener(this);
            //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해둡니다.

            // 클릭시 이벤트 처리 // 수정 기능
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {

                        Intent intent = new Intent(mContext, AddOrEdit.class);
                        intent.putExtra("Position",pos);
                        intent.putExtra("Type","EDIT");
                        mContext.startActivity(intent);
                        notifyItemChanged(pos);

                    }
                }
            });


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
                        mData.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mData.size());
                        break;
                }
                return true;
            }
        };

    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public CustomAdapter(Context context, ArrayList<Color> list, int[] colorlist) {
        mData = list;
        mContext = context;
        Colorlist = colorlist;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.history_item, parent, false);
        CustomAdapter.ViewHolder vh = new CustomAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position) {
        holder.date.setText(mData.get(position).getDate());
        holder.pink.setText(mData.get(position).getPink());
        holder.orange.setText(mData.get(position).getOrange());
        holder.green.setText(mData.get(position).getGreen());
        holder.blue.setText(mData.get(position).getBlue());
        holder.purple.setText(mData.get(position).getPurple());

        holder.box_pink.setBackgroundColor(Colorlist[0]);
        holder.box_orange.setBackgroundColor(Colorlist[1]);
        holder.box_green.setBackgroundColor(Colorlist[2]);
        holder.box_blue.setBackgroundColor(Colorlist[3]);
        holder.box_purple.setBackgroundColor(Colorlist[4]);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return (null != mData ? mData.size() : 0);
    }

   }

