/** 
 * TuSdkDemo
 * EditMultipleComponentSimple.java
 *
 * @author 		Clear
 * @Date 		2015-4-21 下午1:38:04 
 * @Copyright 	(c) 2015 Lasque. All rights reserved.
 * 
 */
package vienan.app.cardgallery;

import android.app.Activity;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.TuEditMultipleComponent;
import org.lasque.tusdk.impl.components.base.TuSdkComponent.TuSdkComponentDelegate;
import org.lasque.tusdk.impl.components.base.TuSdkHelperComponent;

/**
 * 多功能图片编辑组件范例
 * 
 * @author Clear
 */
public class EditMultipleComponentSimple extends SimpleBase
{
	TuSdkComponentDelegate delegate;
	/**
	 * 多功能图片编辑组件范例
	 */
	public EditMultipleComponentSimple()
	{
		super(2, R.string.simple_EditMultipleComponent);
	}

	/**
	 * 显示范例
	 * 
	 * @param activity
	 */
	@Override
	public void showSimple(Activity activity)
	{
		if (activity == null) return;
		// see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/base/TuSdkHelperComponent.html
		this.componentHelper = new TuSdkHelperComponent(activity);

		TuSdk.albumCommponent(activity, new TuSdkComponentDelegate()
		{
			@Override
			public void onComponentFinished(TuSdkResult result, Error error,
					TuFragment lastFragment)
			{
				openEditMultiple(result, error, lastFragment);
			}
		}).showComponent();
	}

	/**
	 * 开启多功能图片编辑
	 * 
	 * @param result
	 * @param error
	 * @param lastFragment
	 */
	private void openEditMultiple(TuSdkResult result, Error error,
			TuFragment lastFragment)
	{
		if (result == null || error != null) return;

		// 组件选项配置
		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/TuEditMultipleComponent.html
		TuEditMultipleComponent component = null;

		if (lastFragment == null)
		{
			component = TuSdk.editMultipleCommponent(
					this.componentHelper.activity(), delegate);
		}
		else
		{
			component = TuSdk.editMultipleCommponent(lastFragment, delegate);
		}

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/TuEditMultipleComponentOption.html
		// component.componentOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/edit/TuEditMultipleOption.html
		// component.componentOption().editMultipleOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/edit/TuEditCuterOption.html
		// component.componentOption().editCuterOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/filter/TuEditFilterOption.html
		// component.componentOption().editFilterOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/filter/TuEditSkinOption.html
		// component.componentOption().editSkinOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/sticker/TuEditStickerOption.html
		// component.componentOption().editStickerOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/filter/TuEditAdjustOption.html
		// component.componentOption().editAdjustOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/filter/TuEditSharpnessOption.html
		// component.componentOption().editSharpnessOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/filter/TuEditApertureOption.html
		// component.componentOption().editApertureOption()

		// @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/filter/TuEditVignetteOption.html
		// component.componentOption().editVignetteOption()


		// 设置图片

		component.setImage(result.image)
				// 设置系统照片
				.setImageSqlInfo(result.imageSqlInfo)
				// 设置临时文件
				.setTempFilePath(result.imageFile)
				// 在组件执行完成后自动关闭组件
				.setAutoDismissWhenCompleted(true)
				// 开启组件
				.showComponent();
	}
}
