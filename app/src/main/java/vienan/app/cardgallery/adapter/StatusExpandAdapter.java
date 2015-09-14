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
import com.daimajia.swipe.SwipeLayout;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.drakeet.materialdialog.MaterialDialog;
import vienan.app.cardgallery.R;
import vienan.app.cardgallery.model.CardModel;
import vienan.app.cardgallery.model.ChildStatusEntity;
import vienan.app.cardgallery.model.GroupStatusEntity;
import vienan.app.cardgallery.utils.ImageUtils;

public class StatusExpandAdapter extends BaseExpandableListAdapter{
	private LayoutInflater inflater = null;
	private List<GroupStatusEntity> groupList;
	private Context context;
	private List<SwipeLayout> swipeLayouts=new ArrayList<SwipeLayout>();
	public List<GroupStatusEntity> getGroupList() {
		return groupList;
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param group_list
	 */
	public StatusExpandAdapter(Context context,
			List<GroupStatusEntity> group_list) {
		this.groupList = group_list;
		this.context=context;
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

		GroupViewHolder holder = new GroupViewHolder();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group_status_item, null);
		}
		holder.groupName = (TextView) convertView
				.findViewById(R.id.one_status_name);
		holder.groupName.setText(groupList.get(groupPosition).getGroupName());

		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder viewHolder = null;
		ChildStatusEntity entity = (ChildStatusEntity) getChild(groupPosition,
				childPosition);
		final CardModel model=new Select().from(CardModel.class)
										.where("Id=?",entity.getCardModel().getId()).executeSingle();
		Log.d("model", "" + model.getId() + model.toString());
		if (convertView != null) {
			viewHolder = (ChildViewHolder) convertView.getTag();
		} else {
			viewHolder = new ChildViewHolder();
			convertView = inflater.inflate(R.layout.child_status_item, null);
			viewHolder.card_type= (ImageView) convertView.findViewById(R.id.iv_type);
			viewHolder.twoStatusTime = (TextView) convertView
					.findViewById(R.id.two_complete_time);
			viewHolder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.sample);
			viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
			viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewWithTag("Bottom2"));
			swipeLayouts.add(viewHolder.swipeLayout);
			viewHolder.iv_star= (ImageView) convertView.findViewById(R.id.star);
			viewHolder.iv_trash= (ImageView) convertView.findViewById(R.id.trash);
			convertView.setTag(viewHolder);
		}

		if (model.star == 0) {
			viewHolder.iv_star.setImageResource(R.mipmap.ic_star_border_white_48dp);
		}else {
			viewHolder.iv_star.setImageResource(R.mipmap.ic_star_white_48dp);
		}

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
							((ImageView) v).setImageResource(R.mipmap.ic_star_white_48dp);
							model.star = 1;
							new Update(CardModel.class).set("star=?",model.star).where("Id=?",model.getId()).execute();
							//notifyDataSetChanged();
							sweetAlertDialog.cancel();
						}
					}).show();

				} else if (model.star == 1) {
					dialog.setTitleText("取消收藏").setContentText("从收藏夹中移除？");
					dialog.setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							((ImageView) v).setImageResource(R.mipmap.ic_star_border_white_48dp);
							model.star = 0;
							new Update(CardModel.class).set("star=?",model.star).where("Id=?",model.getId()).execute();
							//notifyDataSetChanged();
							sweetAlertDialog.cancel();
						}
					}).show();

				}
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
								// reuse previous dialog instance, keep widget user state, reset them if you need
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
		if (model.type.equals("cardNote")){
			Picasso.with(context).load(R.mipmap.ic_image_white_24dp).into(viewHolder.card_type);
			viewHolder.card_type.setBackgroundResource(R.drawable.circle_type);
		}else {
			Picasso.with(context).load(R.mipmap.ic_mode_edit_white_24dp).into(viewHolder.card_type);
			viewHolder.card_type.setBackgroundResource(R.drawable.type_edit);
		}
		viewHolder.twoStatusTime.setText(model.title);
		convertView.findViewById(R.id.item_surface).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Visibility", "" + v.getVisibility());
				if (v.getVisibility() != View.VISIBLE) {
					return;
				}
				final MaterialDialog mMaterialDialog = new MaterialDialog(context).setCanceledOnTouchOutside(true);
				String card_type = model.type;
				if (card_type.equals("note")) {
					View view=LayoutInflater.from(context).inflate(R.layout.text_detail,null);
					TextView textView= (TextView) view.findViewById(R.id.descriptionTextView);
					textView.setText(model.description);
					mMaterialDialog.setTitle(model.title)
							.setContentView(view);
				} else {
					final View cardView = inflater.inflate(R.layout.card_big_img, null);
					ImageView imageView = (ImageView) cardView.findViewById(R.id.imageView);
					TextView title = (TextView) cardView.findViewById(R.id.titleTextView);
					TextView description = (TextView) cardView.findViewById(R.id.descriptionTextView);
					ImageUtils.imageLoader.displayImage(
							ImageDownloader.Scheme.FILE.wrap(model.imgPath)
							, imageView, ImageUtils.options);
					title.setText(model.title);
					description.setText(model.description);
					mMaterialDialog.setView(cardView);
				}
				mMaterialDialog.show();
			}
		});

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public void closeAllItem(){
		for (int i = 0; i <swipeLayouts.size() ; i++) {
			swipeLayouts.get(i).close();
		}
	}
	private class GroupViewHolder {
		ImageView groupImg;
		TextView groupName;
	}

	private class ChildViewHolder {
		public SwipeLayout swipeLayout;
		public ImageView iv_star,iv_trash;
		public ImageView card_type;
		public TextView twoStatusTime;
	}
}
