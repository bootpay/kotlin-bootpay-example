package kr.co.bootpay

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootExtra
import kr.co.bootpay.model.BootUser

class MainActivity : FragmentActivity() {

    val application_id = "5b8f6a4d396fa665fdc2b5e8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BootpayAnalytics.init(this, application_id)

        findViewById<Button>(R.id.button_first).setOnClickListener {
            goBootpayRequest()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun goBootpayRequest() {
        val bootUser = BootUser().setPhone("010-1234-5678")
        val bootExtra = BootExtra().setQuotas(intArrayOf(0, 2, 3))

        val stuck = 1 //재고 있음

        Bootpay.init(this)
            .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id 값
            .setContext(this)
            .setBootUser(bootUser)
            .setBootExtra(bootExtra)
            .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
            .setName("맥북프로's 임다") // 결제할 상품명
            .setOrderId("1234") // 결제 고유번호expire_month
            .setPrice(10000) // 결제할 금액
            .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
            .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
            .onConfirm { message ->
                if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                Log.d("confirm", message);
            }
            .onDone { message ->
                Log.d("done", message)
            }
            .onReady { message ->
                Log.d("ready", message)
            }
            .onCancel { message ->
                Log.d("cancel", message)
            }
            .onError{ message ->
                Log.d("error", message)
            }
            .onClose { message ->
                Log.d("close", "close")
            }
            .request();
    }
}