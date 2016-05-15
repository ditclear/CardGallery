package vienan.app.cardgallery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.model.ChildStatusEntity;
import vienan.app.cardgallery.model.GroupStatusEntity;
import vienan.app.cardgallery.utils.Utils;

public class StatusExpandAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater = null;
    private List<GroupStatusEntity> groupList;
    private Context context;
    public List<GroupStatusEntity> getGroupList() {
        return groupList;
    }

    public TextDrawable noteDrawable = TextDrawable.builder()
            .beginConfig()
            .width(60)  // width in px
            .height(60) // height in px
            .endConfig()
            .buildRound("N", ColorGenerator.DEFAULT.getRandomColor());

    /**
     * 构造方法
     *
     * @param context
     * @param group_list
     */
    public StatusExpandAdapter(Context context,
                               List<GroupStatusEntity> group_list) {
        this.groupList = group_list;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 返回一级Item总数
     */
    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    /**
     * 返回二级Item总数
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupList.get(groupPosition).getChildList() == null) {
            return 0;
        } else {
            return groupList.get(groupPosition).getChildList().size();
        }
    }

    /**
     * 获取一级Item内容
     */
    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    /**
     * 获取二级Item内容
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_status_item, null);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.groupName.setText(groupList.get(groupPosition).getGroupName());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;
        ChildStatusEntity entity = (ChildStatusEntity) getChild(groupPosition,
                childPosition);
        final CardModel model = new Select().from(CardModel.class)
                .where("Id=?", entity.getCardModel().getId()).executeSingle();
        Log.d("model", "" + model.getId() + model.toString());
        if (convertView != null) {
            viewHolder = (ChildViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.child_status_item, null);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        if (model.star == 0) {
            Picasso.with(context).load(R.mipmap.ic_star_border_white_48dp).into(viewHolder.iv_star);
        } else {
            Picasso.with(context).load(R.mipmap.ic_star_white_48dp).into(viewHolder.iv_star);
        }
        final ChildViewHolder finalViewHolder = viewHolder;
        viewHolder.iv_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setCancelText("不确定")
                        .showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        });
                if (model.star == 0) {
                    dialog.setTitleText("收藏").setContentText("添加到我的收藏夹？");
                    dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Picasso.with(context).load(R.mipmap.ic_star_white_48dp).into(finalViewHolder.iv_star);
                            model.star = 1;
                            new Update(CardModel.class).set("star=?", model.star).where("Id=?", model.getId()).execute();
                            sweetAlertDialog.cancel();
                            finalViewHolder.swipeLayout.close(true);
                            Utils.toast(context,"已添加收藏");
                        }
                    });

                } else if (model.star == 1) {
                    dialog.setTitleText("取消收藏").setContentText("从收藏夹中移除？");
                    dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Picasso.with(context).load(R.mipmap.ic_star_border_white_48dp).into(finalViewHolder.iv_star);
                            model.star = 0;
                            new Update(CardModel.class).set("star=?", model.star).where("Id=?", model.getId()).execute();
                            sweetAlertDialog.cancel();
                            finalViewHolder.swipeLayout.close(true);
                            Utils.toast(context,"取消收藏");
                        }
                    });

                }
                dialog.show();
            }
        });
        viewHolder.iv_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Note将会被删除!")
                        .setCancelText("不删")
                        .setConfirmText("删")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.setTitleText("Cancelled!")
                                        .setContentText("操作取消 :)")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                CardModel.delete(CardModel.class, model.getId());
                                groupList.get(groupPosition).getChildList().remove(childPosition);
                                if (groupList.get(groupPosition).getChildList().size() == 0) {
                                    groupList.remove(groupPosition);
                                }
                                finalViewHolder.swipeLayout.close(true);
                                notifyDataSetChanged();
                                sDialog.setTitleText("Deleted!")
                                        .setContentText("Note已被删除!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });
        if (model.type.equals("cardNote")) {
            Picasso.with(context).load(new File(model.imgPath)).noFade().into(viewHolder.card_type);
        } else {
            viewHolder.card_type.setImageDrawable(noteDrawable);
        }
        viewHolder.twoStatusTime.setText(model.title);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    static class GroupViewHolder {
        ImageView groupImg;
        @Bind(R.id.one_status_name)
        TextView groupName;

        public GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @Bind(R.id.sample)
        SwipeLayout swipeLayout;
        @Bind(R.id.star)
        ImageView iv_star;
        @Bind(R.id.trash)
        ImageView iv_trash;
        CircleImageView card_type;
        @Bind(R.id.two_complete_time)
        TextView twoStatusTime;

        public ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewWithTag("Bottom2"));
            card_type = (CircleImageView) view.findViewById(R.id.iv_type);
        }
    }
}
