package jp.techacademy.shiori.tazawa.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    // パーミッション用
    private val PERMISSIONS_REQUEST_CODE = 100

    // タイマー用
    private var mTimer: Timer? = null
    private var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // バージョンごとのパーミッション確認
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている場合は画像情報を取得する
                // Log.d("kotlintest", "許可された")

            } else {
                // 許可されていないので許可ダイアログを表示する
                // →onRequestPermissionsResultメソッドでユーザーの選択結果を受け取る

                //  第1引数には許可を求めたいPermissionを配列で、
                //  第2引数には結果を受け取る際に識別するための数値を与える
                requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_CODE)
            }

            // Android 5.0系以下の場合
        } else {
            // Log.d("kotlintest", "Android5以下")
        }


        // ボタンを押したときの処理

        val resolver = contentResolver

        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null=全項目）
                null, // フィルタ条件（null=フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート（null=ソートなし）
        )

        // 進むボタンを押したときの処理
        button1.setOnClickListener {

            // 1つも画像がない場合には最初のif文を抜ける
            if (cursor!!.getCount() != 0) {
                // カーソルが最後のときは、最初に戻る
                // そうでないときはカーソルを進める
                if (cursor.isLast()) {
                    cursor.moveToFirst()
                } else {
                    cursor.moveToNext()
                }

                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }

        }

        // 戻るボタンを押したときの処理
        button2.setOnClickListener {

            // 1つも画像がない場合には最初のif文を抜ける
            if (cursor!!.getCount() != 0) {

                // カーソルが最初のときは、最後に進む
                // そうでないときはカーソルを戻す
                if (cursor.isFirst()) {
                    cursor.moveToLast()
                } else {
                    cursor.moveToPrevious()
                }

                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }


        }

        // 再生/停止ボタンを押したときの処理
        button3.setOnClickListener {

            // 停止しているときに押した場合＝再生ボタン
            if (mTimer == null) {

                // ボタンの表示を「停止」に
                button3.text = "停止"

                // 進むボタン、戻るボタンをタップ不可に
                button1.isEnabled = false
                button2.isEnabled = false

                // タイマーの生成
                mTimer = Timer()

                // タイマーの始動
                mTimer!!.schedule(object : TimerTask() {

                    // button1と同様の処理
                    override fun run() {
                        mHandler.post {
                            // 1つも画像がない場合には最初のif文を抜ける
                            if (cursor!!.getCount() != 0) {
                                // カーソルが最後のときは、最初に戻る
                                // そうでないときはカーソルを進める

                                if (cursor.isLast()) {
                                    cursor.moveToFirst()
                                } else {
                                    cursor.moveToNext()
                                }

                                // indexからIDを取得し、そのIDから画像のURIを取得する
                                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                imageView.setImageURI(imageUri)
                            }


                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで2000ミリ秒（2秒）、ループの間隔を2000ミリ秒（2秒） に設定


            // 再生しているときに押した場合＝停止ボタン
            } else {

                // ボタンの表示を「再生」に
                button3.text = "再生"

                // 進むボタン、戻るボタンをタップ可能に
                button1.isEnabled = true
                button2.isEnabled = true

                // タイマーを止めて破棄する
                mTimer!!.cancel()
                mTimer = null

            }
        }
    }

    // パーミッション許可を求めたときの結果の確認
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // テキストにはないけど必要？
        // リクエストコード（requestCode）が、許可を求めたもの（PERMISSIONS_REQUEST_CODE）と一致した場合
        // （どの許可ダイアログの結果か確認をするため）
        when (requestCode){
            PERMISSIONS_REQUEST_CODE ->

                // 今回は1つしか許可を求めていないので、結果はgrantResults[0]に格納されている
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 許可された
                } else {
                    // 許可されなかった場合はメッセージを表示してアプリを終了する

                    showAlertDialog()
                }
        }
    }

    // 許可されなかったときのアラートメッセージ
    private fun showAlertDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        // alertDialogBuilder.setTitle("Alert")
        alertDialogBuilder.setMessage("外部ストレージへのパーミッションを許可してください")

        // OKボタンを押すとアプリを終了する
        alertDialogBuilder.setPositiveButton("OK"){_,_ ->
            // finish()
        }

        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}