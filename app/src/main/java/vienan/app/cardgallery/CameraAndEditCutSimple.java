/**
 * TuSdkDemo
 * CameraAndEditCutSimple.java
 *
 * @author Lasque
 * @Date 2015-8-7 下午1:40:10
 * @Copyright (c) 2015 Lasque. All rights reserved.
 */
package vienan.app.cardgallery;

import android.app.Activity;

import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.hardware.CameraHelper;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.base.TuSdkHelperComponent;
import org.lasque.tusdk.impl.components.camera.TuCameraFragment;
import org.lasque.tusdk.impl.components.camera.TuCameraFragment.TuCameraFragmentDelegate;
import org.lasque.tusdk.impl.components.camera.TuCameraOption;
import org.lasque.tusdk.impl.components.edit.TuEditTurnAndCutFragment;
import org.lasque.tusdk.impl.components.edit.TuEditTurnAndCutOption;


/**
 * 相机连接图片编辑组件范例
 *
 * @author Lasque
 */
public class CameraAndEditCutSimple extends SimpleBase implements
        TuCameraFragmentDelegate
{
    /**
     * 相机连接图片编辑组件范例
     */
    public CameraAndEditCutSimple()
    {
        super(2, R.string.simple_CameraAndEditCutComponent);
    }

    /**
     * 显示范例
     */
    @Override
    public void showSimple(Activity activity)
    {
        if (activity == null) return;

        // 如果不支持摄像头显示警告信息
        if (CameraHelper.showAlertIfNotSupportCamera(activity)) return;
        // 组件选项配置
        // @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/camera/TuCameraOption.html
        TuCameraOption option = new TuCameraOption();

        // 控制器类型
        // option.setComponentClazz(TuCameraFragment.class);

        // 设置根视图布局资源ID
        // option.setRootViewLayoutId(TuCameraFragment.getLayoutId());

        // 保存到临时文件 (默认不保存, 当设置为true时, TuSdkResult.imageFile, 处理完成后将自动清理原始图片)
        // option.setSaveToTemp(true);

        // 保存到系统相册 (默认不保存, 当设置为true时, TuSdkResult.sqlInfo, 处理完成后将自动清理原始图片)
        // option.setSaveToAlbum(true);

        // 保存到系统相册的相册名称
        // option.setSaveToAlbumName("TuSdk");

        // 照片输出压缩率 (默认:90，0-100 如果设置为0 将保存为PNG格式)
        // option.setOutputCompress(90);

        // 相机方向 (默认:CameraInfo.CAMERA_FACING_BACK){@link
        // android.hardware.Camera.CameraInfo}
        // option.setAvPostion(CameraInfo.CAMERA_FACING_BACK);

        // 照片输出图片长宽 (默认：全屏)
        // option.setOutputSize(new TuSdkSize(1440, 1920));

        // 闪关灯模式
        // option.setDefaultFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        // 是否开启滤镜支持 (默认: 关闭)
        // option.setEnableFilters(true);

        // 默认是否显示滤镜视图 (默认: 不显示, 如果mEnableFilters = false, mShowFilterDefault将失效)
        // option.setShowFilterDefault(true);

        // 滤镜组行视图宽度 (单位:DP)
        // option.setGroupFilterCellWidthDP(75);

        // 滤镜组选择栏高度 (单位:DP)
        // option.setFilterBarHeightDP(100);

        // 滤镜分组列表行视图布局资源ID (默认:
        // tusdk_impl_component_widget_group_filter_group_view，如需自定义请继承自
        // GroupFilterGroupView)
        // option.setGroupTableCellLayoutId(GroupFilterGroupView.getLayoutId());

        // 滤镜列表行视图布局资源ID (默认:
        // tusdk_impl_component_widget_group_filter_item_view，如需自定义请继承自
        // GroupFilterItemView)
        // option.setFilterTableCellLayoutId(GroupFilterItemView.getLayoutId());

        // 开启滤镜配置选项
        // option.setEnableFilterConfig(true);

        // 需要显示的滤镜名称列表 (如果为空将显示所有自定义滤镜)
        // 滤镜名称参考 TuSDK.bundle/others/lsq_tusdk_configs.json
        // filterGroups[]->filters[]->name lsq_filter_%{Brilliant}
        // String[] filters = { "SkinNature", "SkinPink", "SkinJelly",
        // "SkinNoir",
        // "SkinRuddy", "SkinPowder", "SkinSugar" };
        // option.setFilterGroup(Arrays.asList(filters));

        // 是否保存最后一次使用的滤镜
        // option.setSaveLastFilter(true);

        // 自动选择分组滤镜指定的默认滤镜
        // option.setAutoSelectGroupDefaultFilter(true);

        // 开启用户滤镜历史记录
        // option.setEnableFiltersHistory(true);

        // 显示滤镜标题视图
        // option.setDisplayFiltersSubtitles(true);

        // 触摸聚焦视图ID (默认: tusdk_impl_component_camera_focus_touch_view)
        // option.setFocusTouchViewId(TuFocusTouchView.getLayoutId());

        // 视频视图显示比例 (默认: 0, 全屏)
        // option.setCameraViewRatio(0);

        // 是否直接输出图片数据 (默认:false，输出已经处理好的图片Bitmap)
        // option.setOutputImageData(false);

        // 开启系统拍照声音 (默认:true)
        // option.setEnableCaptureSound(true);

        // 自定义拍照声音RAW ID，默认关闭系统发声
        // option.setCaptureSoundRawId(R.raw.lsq_camera_focus_beep);

        // 自动释放相机在拍摄后 (节省手机内存, 需要手动再次启动)
        // option.setAutoReleaseAfterCaptured(false);

        // 开启长按拍摄 (默认：false)
        option.setEnableLongTouchCapture(true);

        // 开启聚焦声音 (默认:true)
        // option.setEnableFocusBeep(true);

        // 是否需要统一配置参数 (默认false, 取消三星默认降噪，锐化)
        // option.setUnifiedParameters(false);

        // 预览视图实时缩放比例 (默认:0.7f, 实时预览时，缩小到全屏大小比例，提升预览效率， 0 < mPreviewEffectScale
        // <= 1)
        // option.setPreviewEffectScale(0.7f);

        // 视频覆盖区域颜色 (默认：0xFF000000)
        // option.setRegionViewColor(0xFF000000);

        // 禁用前置摄像头自动水平镜像 (默认: false，前置摄像头拍摄结果自动进行水平镜像)
        // option.setDisableMirrorFrontFacing(true);

        TuCameraFragment fragment = option.fragment();
        fragment.setDelegate(this);
        // see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/base/TuSdkHelperComponent.html
        this.componentHelper = new TuSdkHelperComponent(activity);
        // 开启相机
        this.componentHelper.presentModalNavigationActivity(fragment, true);
    }

    /**
     * 获取一个拍摄结果
     *
     * @param fragment 默认相机视图控制器
     * @param result   拍摄结果
     */
    @Override
    public void onTuCameraFragmentCaptured(TuCameraFragment fragment,
                                           TuSdkResult result)
    {
        openTuEditTurnAndCut(result, null, fragment);
    }

    /**
     * 获取一个拍摄结果 (异步方法)
     *
     * @param fragment 默认相机视图控制器
     * @param result   拍摄结果
     * @return 是否截断默认处理逻辑 (默认: false, 设置为True时使用自定义处理逻辑)
     */
    @Override
    public boolean onTuCameraFragmentCapturedAsync(TuCameraFragment fragment,
                                                   TuSdkResult result)
    {
        TLog.d("onTuCameraFragmentCapturedAsync: %s", result);
        return false;
    }

    @Override
    public void onComponentError(TuFragment fragment, TuSdkResult result,
                                 Error error)
    {
        TLog.d("onComponentError: fragment - %s, result - %s, error - %s",
                fragment, result, error);
    }

    /**
     * 开启图片编辑组件 (裁剪)
     *
     * @param result       返回结果
     * @param error        异常信息
     * @param lastFragment 最后显示的控制器
     */
    private void openTuEditTurnAndCut(TuSdkResult result, Error error,
                                      TuFragment lastFragment)
    {
        if (result == null) return;

        // 组件选项配置
        // @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/edit/TuEditTurnAndCutOption.html
        TuEditTurnAndCutOption option = new TuEditTurnAndCutOption();

        // 是否开启滤镜支持 (默认: 关闭)
        option.setEnableFilters(true);
        // 开启用户滤镜历史记录
        option.setEnableFiltersHistory(true);

        // 需要裁剪的长宽
        option.setCutSize(new TuSdkSize(640, 640));

        // 是否在控制器结束后自动删除临时文件
        option.setAutoRemoveTemp(true);

        // 是否显示处理结果预览图 (默认：关闭，调试时可以开启)
        option.setShowResultPreview(false);

        // 保存到系统相册 (默认不保存, 当设置为true时, TuSdkResult.sqlInfo, 处理完成后将自动清理原始图片)
        option.setSaveToAlbum(true);

        TuEditTurnAndCutFragment fragment = option.fragment();

        // 输入的图片对象 (处理优先级: Image > TempFilePath > ImageSqlInfo)
        fragment.setImage(result.image);
        fragment.setTempFilePath(result.imageFile);
        fragment.setImageSqlInfo(result.imageSqlInfo);

        fragment.setDelegate(mDelegate);

        // 如果lastFragment不存在，您可以使用如下方法开启fragment
        if (lastFragment == null)
        {
            this.componentHelper.presentModalNavigationActivity(fragment);
        } else
        {
            // 开启图片编辑组件 (裁剪)
            lastFragment.pushFragment(fragment);

            // 关闭全屏
            lastFragment.wantFullScreen(false);
        }
    }

    TuEditTurnAndCutFragment.TuEditTurnAndCutFragmentDelegate mDelegate =
                new TuEditTurnAndCutFragment.TuEditTurnAndCutFragmentDelegate()
    {
        /**
         * 图片编辑完成
         *
         * @param fragment 旋转和裁剪视图控制器
         * @param result   旋转和裁剪视图控制器处理结果
         */
        @Override
        public void onTuEditTurnAndCutFragmentEdited(
                TuEditTurnAndCutFragment fragment, TuSdkResult result)
        {

            if (!fragment.isShowResultPreview())
            {
                fragment.hubDismissRightNow();
                fragment.dismissActivityWithAnim();
            }
            TLog.d("onTuEditTurnAndCutFragmentEdited: %s", result);
        }

        /**
         * 图片编辑完成 (异步方法)
         *
         * @param fragment 旋转和裁剪视图控制器
         * @param result   旋转和裁剪视图控制器处理结果
         * @return 是否截断默认处理逻辑 (默认: false, 设置为True时使用自定义处理逻辑)
         */
        @Override
        public boolean onTuEditTurnAndCutFragmentEditedAsync(
                TuEditTurnAndCutFragment fragment, TuSdkResult result)
        {
            return false;
        }

        @Override
        public void onComponentError(TuFragment fragment, TuSdkResult result,
                                     Error error)
        {
            TLog.d("onComponentError: fragment - %s, result - %s, error - %s",
                    fragment, result, error);
        }
    };
}