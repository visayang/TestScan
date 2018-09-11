package com.example.qr_codescan;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.app.zxing.encoding.EncodingHandler;
import com.test.testscan.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public class MainActivity extends Activity implements OnClickListener{
	private final static int SCANNIN_GREQUEST_CODE = 1;
	/**
	 * 显示扫描结果
	 */
	private TextView mTextView ;
	/**
	 * 显示扫描拍的图片
	 */
	private ImageView mImageView;
	private EditText edit_qr,edit_r;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.result);
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
		edit_qr = (EditText) findViewById(R.id.edit_qr);
		edit_r = (EditText) findViewById(R.id.edit_r);

		//点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
		//扫描完了之后调到该界面
		findViewById(R.id.btn_scan_qr).setOnClickListener(this);
		findViewById(R.id.btn_create_qr).setOnClickListener(this);
		findViewById(R.id.btn_create_r).setOnClickListener(this);


		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
			// 申请多个权限。
			AndPermission.with(this)
					.requestCode(REQUEST_CODE_PERMISSION_SD)
					.permission(Manifest.permission.READ_EXTERNAL_STORAGE)
					// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
					.rationale(rationaleListener
					)
					.send();
		}else{
			Toast.makeText(this, "11111已经授权读写SD权限了", Toast.LENGTH_SHORT).show();
		}
	}

	private static final int REQUEST_CODE_PERMISSION_SD = 101;
	private static final int REQUEST_CODE_PERMISSION_CAMERA = 102;

	private static final int REQUEST_CODE_SETTING = 300;
	private RationaleListener rationaleListener = new RationaleListener() {
		@Override
		public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
			// 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
			// AndPermission.rationaleDialog(Context, Rationale).show();

			// 自定义对话框。
			switch (requestCode){
				case REQUEST_CODE_PERMISSION_SD:
					com.yanzhenjie.alertdialog.AlertDialog.build(MainActivity.this)
							.setTitle(R.string.title_dialog)
							.setMessage(R.string.message_permission_rationale)
							.setPositiveButton(R.string.btn_dialog_yes_permission, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									rationale.resume();
								}
							})

							.setNegativeButton(R.string.btn_dialog_no_permission, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									rationale.cancel();
								}
							})
							.show();
					break;
				case REQUEST_CODE_PERMISSION_CAMERA:
					com.yanzhenjie.alertdialog.AlertDialog.build(MainActivity.this)
							.setTitle(R.string.title_dialog3)
							.setMessage(R.string.message_permission_rationale3)
							.setPositiveButton(R.string.btn_dialog_yes_permission, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									rationale.resume();
								}
							})

							.setNegativeButton(R.string.btn_dialog_no_permission, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									rationale.cancel();
								}
							})
							.show();
					break;
			}
		}
	};
	//----------------------------------拍照  权限----------------------------------//

	@PermissionYes(REQUEST_CODE_PERMISSION_CAMERA)
	private void getCAMERAYes(List<String> grantedPermissions) {
		Toast.makeText(this, R.string.message_post_succeed3, Toast.LENGTH_SHORT).show();
	}

	@PermissionNo(REQUEST_CODE_PERMISSION_CAMERA)
	private void getCAMERANo(List<String> deniedPermissions) {
		Toast.makeText(this, R.string.message_post_failed3, Toast.LENGTH_SHORT).show();

		// 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
		if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
			AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
					.setTitle(R.string.title_dialog3)
					.setMessage(R.string.message_permission_failed3)
					.setPositiveButton(R.string.btn_dialog_yes_permission)
					.setNegativeButton(R.string.btn_dialog_no_permission, null)
					.show();
		}
	}


	//----------------------------------SD  权限----------------------------------//

	@PermissionYes(REQUEST_CODE_PERMISSION_SD)
	private void getMultiYes(List<String> grantedPermissions) {
		Toast.makeText(this, R.string.message_post_succeed, Toast.LENGTH_SHORT).show();
	}

	@PermissionNo(REQUEST_CODE_PERMISSION_SD)
	private void getMultiNo(List<String> deniedPermissions) {
		Toast.makeText(this, R.string.message_post_failed, Toast.LENGTH_SHORT).show();

		// 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
		if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
			AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
					.setTitle(R.string.title_dialog2)
					.setMessage(R.string.message_permission_failed)
					.setPositiveButton(R.string.btn_dialog_yes_permission)
					.setNegativeButton(R.string.btn_dialog_no_permission, null)
					.show();
		}
	}

	//----------------------------------权限回调处理----------------------------------//

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
			grantResults) {

		Log.i("ta", "============");
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		/**
		 * 转给AndPermission分析结果。
		 *
		 * @param object     要接受结果的Activity、Fragment。
		 * @param requestCode  请求码。
		 * @param permissions  权限数组，一个或者多个。
		 * @param grantResults 请求结果。
		 */
		AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
		Log.i("ta", "！！！！！！！！！！！！！");
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_scan_qr:
				if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
					// 申请多个权限。
					AndPermission.with(this)
							.requestCode(REQUEST_CODE_PERMISSION_CAMERA)
							.permission(Manifest.permission.CAMERA)
							// rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
							.rationale(rationaleListener
							)
							.send();
				}else{
					Toast.makeText(this, "11111已经授权读写SD权限了", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MipcaActivityCapture.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				}
				break;
			case R.id.btn_create_qr:
			{
				String contentString = edit_qr.getText().toString();
				if (!contentString.equals("")) {
					//根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
					Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
					if(qrCodeBitmap!=null) mImageView.setImageBitmap(qrCodeBitmap);
					else Toast.makeText(this, "解析错误", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(this, "Text can not be empty", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case R.id.btn_create_r:
			{
				String contentString = edit_r.getText().toString();
				if (!contentString.equals("")) {
					//根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
					Bitmap qrCodeBitmap = EncodingHandler.creatBarcode(this, contentString, 350, 150, true);
					if(qrCodeBitmap!=null) mImageView.setImageBitmap(qrCodeBitmap);
					else Toast.makeText(this, "解析错误", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(MainActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if(resultCode == RESULT_OK){
					Bundle bundle = data.getExtras();
					//显示扫描到的内容
					mTextView.setText(bundle.getString("result"));
					//显示
					mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
				}
				break;
			case REQUEST_CODE_SETTING:
			case REQUEST_CODE_PERMISSION_CAMERA:
				Toast.makeText(this, R.string.message_setting_back, Toast.LENGTH_LONG).show();
				//设置成功，再次请求更新
				break;

		}
	}
}
