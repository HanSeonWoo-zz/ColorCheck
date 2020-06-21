package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Color> mData;
    private Context mContext;

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


        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            date = itemView.findViewById(R.id.recycler_tv_date);
            pink = itemView.findViewById(R.id.tv_pink);
            orange = itemView.findViewById(R.id.tv_orange);
            green = itemView.findViewById(R.id.tv_green);
            blue = itemView.findViewById(R.id.tv_blue);
            purple = itemView.findViewById(R.id.tv_purple);

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


                        // Color cr = mData.get(pos) ;

                        // 다이얼로그 방식
                       /* {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

                            View view = LayoutInflater.from(mContext)
                                    .inflate(R.layout.edit_box, null, false);
                            builder.setView(view);
                            final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);

                            final EditText editdate = view.findViewById(R.id.edit_date);
                            final EditText editStartTime = view.findViewById(R.id.edit_StartTime);
                            final EditText editEndTime = view.findViewById(R.id.edit_EndTime);
                            final EditText editcolor = view.findViewById(R.id.edit_color);


                            // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                            editdate.setText(mData.get(getAdapterPosition()).getDate());
                            editStartTime.setText(mData.get(getAdapterPosition()).getStartTime());
                            editEndTime.setText(mData.get(getAdapterPosition()).getEndTime());
                            editcolor.setText(mData.get(getAdapterPosition()).getColor());


                            final AlertDialog dialog = builder.create();
                            ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                                // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로

                                public void onClick(View v) {
                                    String strdate = editdate.getText().toString();
                                    String strStartTime = editStartTime.getText().toString();
                                    String strEndTime = editEndTime.getText().toString();
                                    String strcolor = editcolor.getText().toString();

                                    Color cr = new Color(strdate, strStartTime, strEndTime, strcolor);

                                    // 8. ListArray에 있는 데이터를 변경하고
                                    mData.set(getAdapterPosition(), cr);


                                    // 9. 어댑터에서 RecyclerView에 반영하도록 합니다.

                                    notifyItemChanged(getAdapterPosition());

                                    dialog.dismiss();
                                }
                            });

                            dialog.show();


                        }*/
                    }
                }
            });


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.

            //MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "삽입(위로)");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            //Edit.setOnMenuItemClickListener(onEditMenu);
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
    public CustomAdapter(Context context, ArrayList<Color> list) {
        mData = list;
        mContext = context;
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

//        holder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//        holder.english.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//        holder.korean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//
//        holder.id.setGravity(Gravity.CENTER);
//        holder.english.setGravity(Gravity.CENTER);
//        holder.korean.setGravity(Gravity.CENTER);

        holder.date.setText(mData.get(position).getDate());
        holder.pink.setText(mData.get(position).getPink());
        holder.orange.setText(mData.get(position).getOrange());
        holder.green.setText(mData.get(position).getGreen());
        holder.blue.setText(mData.get(position).getBlue());
        holder.purple.setText(mData.get(position).getPurple());

//        holder.EndTime.setText(mData.get(position).getEndTime());
//        //holder.color.setText(mData.get(position).getColor());
//        if(mData.get(position).getColor().contentEquals("1")){
//            holder.color.setBackgroundColor(0xFFFE2E9A);
//        }
//        else if(mData.get(position).getColor().contentEquals("2")){
//            holder.color.setBackgroundColor(0xFFFF8000);
//        }
//        else if(mData.get(position).getColor().contentEquals("3")){
//            holder.color.setBackgroundColor(0xFF1E8037);
//        }
//        else if(mData.get(position).getColor().contentEquals("4")){
//            holder.color.setBackgroundColor(0xFF0000FF);
//        }
//        else if(mData.get(position).getColor().contentEquals("5")){
//            holder.color.setBackgroundColor(0xFFA901DB);
//        }
//        else{
//            holder.color.setBackgroundColor(0xFFFFFFFF);
//        }

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return (null != mData ? mData.size() : 0);
    }

   }

