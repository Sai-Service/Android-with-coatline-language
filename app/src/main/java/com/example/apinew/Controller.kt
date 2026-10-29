package com.example.apinew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Controller : AppCompatActivity() {

    private var ouId: Int = 0
    private var locId: Int = 0
    private lateinit var locIdTxt: TextView
    private lateinit var username: TextView
    private lateinit var deptIntent: TextView
    private lateinit var login_name: String
    private lateinit var location_name: String
    private lateinit var attribute1: String
    private lateinit var attribute2: String

    private lateinit var location: String
    private lateinit var logoutButton: ImageView
    private lateinit var deptName: String
    private lateinit var emailId: String
    private lateinit var source: String
    private lateinit var role: String
    private lateinit var dmsLoc:String


    // Delivery-only fields
    private var userContact: String? = null
    private var userName: String? = null

    // ERP deptName + Delivery deptName (both needed when source = "both")
    private lateinit var erpDeptName: String
    private lateinit var deliveryDeptName: String
    private lateinit var about: ImageButton
    private lateinit var row1: View
    private lateinit var row2: View
    private lateinit var row3: View
    private lateinit var row4: View
    private lateinit var row5: View
    private lateinit var row6: View
    private lateinit var row7: View
    private lateinit var row8: View
    private lateinit var row9: View
    private lateinit var row10: View
    private lateinit var row11: View
    private lateinit var row12: View
    private lateinit var row13: View
    private lateinit var row14: View
    private lateinit var row15: View
    private lateinit var row16: View
    private lateinit var row17: View
    private lateinit var row18: View
    private lateinit var row19: View
    private lateinit var row20: View
    private lateinit var row21: View
    private lateinit var row22: View
    private lateinit var row23: View
    private lateinit var row24: View
    private lateinit var rowDelivery1: View
    private lateinit var rowDelivery2: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        // ── Read all intent extras ─────────────────────────────
        source        = intent.getStringExtra("source") ?: "erp"
        ouId          = intent.getIntExtra("ouId", 0)
        locId         = intent.getIntExtra("locId", 0)
        login_name    = intent.getStringExtra("login_name") ?: ""
        location_name = intent.getStringExtra("location_name") ?: ""
        location      = intent.getStringExtra("location") ?: ""
        deptName      = intent.getStringExtra("deptName") ?: ""
        attribute1    = intent.getStringExtra("attribute1") ?: ""
        attribute2    = intent.getStringExtra("attribute2") ?: ""
        emailId       = intent.getStringExtra("emailId") ?: ""
        userContact   = intent.getStringExtra("userContact")
        userName      = intent.getStringExtra("userName")
        role       = intent.getStringExtra("role") ?: ""
        dmsLoc       = intent.getStringExtra("dmsLoc") ?: ""


        erpDeptName      = intent.getStringExtra("erpDeptName") ?: deptName
// ↓ KEY FIX: for pure delivery login, deliveryDeptName falls back to deptName
        deliveryDeptName = intent.getStringExtra("deliveryDeptName")
            ?.takeIf { it.isNotEmpty() } ?: deptName

        // ── Find views ────────────────────────────────────────
        locIdTxt     = findViewById(R.id.locIdTxt)
        username     = findViewById(R.id.username)
        deptIntent   = findViewById(R.id.deptIntent)
        about        = findViewById(R.id.about)
        logoutButton = findViewById(R.id.logoutButton)

        row1  = findViewById(R.id.row1)
        row2  = findViewById(R.id.row2)
        row3  = findViewById(R.id.row3)
        row4  = findViewById(R.id.row4)
        row5  = findViewById(R.id.row5)
        row6  = findViewById(R.id.row6)
        row7  = findViewById(R.id.row7)
        row8  = findViewById(R.id.row8)
        row9  = findViewById(R.id.row9)
        row10 = findViewById(R.id.row10)
        row11 = findViewById(R.id.row11)
        row12 = findViewById(R.id.row12)
        row13 = findViewById(R.id.row13)
        row14 = findViewById(R.id.row14)
        row15 = findViewById(R.id.row15)
        row16 = findViewById(R.id.row16)
        row17 = findViewById(R.id.row17)
        row18 = findViewById(R.id.row18)
        row19 = findViewById(R.id.row19)
        row20 = findViewById(R.id.row20)
        row21 = findViewById(R.id.row21)
        row22 = findViewById(R.id.row22)
        row23 = findViewById(R.id.row23)
        row24 = findViewById(R.id.row24)
        rowDelivery1 = findViewById(R.id.rowDelivery1)
        rowDelivery2 = findViewById(R.id.rowDelivery2)

        // ── Header ────────────────────────────────────────────
        locIdTxt.text   = location_name
        username.text   = login_name
        deptIntent.text = deptName

        // ── Hide all rows first ───────────────────────────────
        hideAllRows()

        // ── Apply visibility based on source ──────────────────
        when (source) {
            "erp"      -> applyErpRoleVisibility(erpDeptName)
            // ↓ FIX: was using deliveryDeptName which was empty for pure delivery login
            "delivery" -> applyDeliveryRoleVisibility(deliveryDeptName)
            "both"     -> {
                applyErpRoleVisibility(erpDeptName)
                applyDeliveryRoleVisibility(deliveryDeptName)
            }
        }

        logoutButton.setOnClickListener { logout() }
        about.setOnClickListener { aboutActivity() }
    }

    // ── Show helper (clickable) ───────────────────────────────
    private fun show(vararg views: View) {
        views.forEach { it.visibility = View.VISIBLE }
    }


    // ── Show helper (visible but disabled/non-clickable) ──────
    private fun showDisabled(vararg views: View) {
        views.forEach {
            it.visibility = View.VISIBLE
            it.isEnabled = false
            it.alpha = 0.4f          // grayed-out look so user knows it's inactive
            it.setOnClickListener(null)   // strip any existing click listener
            it.isClickable = false
            it.isFocusable = false
        }
    }

    // ── Hide every row ────────────────────────────────────────
    private fun hideAllRows() {
        listOf(
            row1, row2, row3, row4, row5, row6, row7, row8,
            row9, row10, row11, row12, row13, row14, row15,
            row16, row17, row18, row19, row20, row21, row22,
            row23, row24, rowDelivery1, rowDelivery2
        ).forEach { it.visibility = View.GONE }
    }

    // ── ERP role → row visibility ─────────────────────────────
    // Takes deptName as parameter so it works for both single
    // ERP login AND merged "both" login
    private fun applyErpRoleVisibility(dept: String) {
        when (dept) {
            "ACCOUNTS" -> show(row1, row2)
            "SECURITY" -> show(row5)
            "SALES" -> show(row1, row3, row4)
            "SUPERADMIN" -> show(row1, row4)
            "TRUEVALUE" -> {
                show(row1, row6, row8); row9.visibility = View.INVISIBLE
            }

            "TVSUPERADMIN" -> {
                show(row1, row6, row8); row9.visibility = View.INVISIBLE
            }

            "TVACCOUNTS" -> show(row1, row9)
            "SERVICE" -> show(row1, row12, row14, row15, row16)
            "DP" -> show(row1, row12, row14, row15, row16)
            "SERACCOUNTS" -> show(row1, row10)
            "SALES-SHOWROOM" -> show(row1, row4)
            "ADMINISTRATOR" -> show(row13)
            "SECURITYTD" -> show(row1, row17, row23)
            "SECURITYPARK" -> show(row1, row23)
            "SERVICE-PARK" -> show(row1, row23)
            "BODYSHOP-PARK" -> show(row1, row23)
            "SALES DEMO" -> show(row1, row19, row20)
            "SALESSUPERADMIN" -> show(row1, row19, row20)
            "WASHING" -> show(row1, row18)
            "VEHWASHGOA" -> show(row1, row24)
            "WM" -> show(row1)
            "WASHSALES" -> show(row1, row22)
            "SERACCTS" -> show(row1, row10)
        }
    }



    // ── Delivery role → row visibility ────────────────────────
    // Replace deptName strings with your actual Delivery role names
    private fun applyDeliveryRoleVisibility(dept: String) {
        when (dept) {
            "SERVICE"        -> show(rowDelivery1, rowDelivery2)
            "DELIVERY"       -> show(rowDelivery1, rowDelivery2)
            "DELIVERY-ADMIN" -> show(rowDelivery1, rowDelivery2)
//            else             -> if (dept.isNotEmpty()) show(rowDelivery1,rowDelivery2)
        }
    }

    // ── Show helper ───────────────────────────────────────────
    // Uses VISIBLE not GONE — safe to call multiple times
    // (calling show() twice on a row just keeps it VISIBLE)


    // ── Navigation functions ──────────────────────────────────

    fun goToCityReport(view: View)               { startWithExtras(StockController::class.java) }
    fun goToChassisReport(view: View)            { startWithExtras(ChassisActivity::class.java) }
    fun goToStockTaking(view: View)              { startWithExtras(StockTaking::class.java) }
    fun goToCamera(view: View)                   { startWithExtras(StockTransferNew::class.java) }
    fun goToTrueValue(view: View)                { startWithExtras(TrueValueStk::class.java) }
    fun SecurityVehTrans(view: View)             { startWithExtras(SecurityVehicleTrack::class.java) }
    fun trueValVehEnq(view: View)                { startWithExtras(TrueValVehicleEnquiry::class.java) }
    fun trueValStockTake(view: View)             { startWithExtras(TrueValueStockTaking2::class.java) }
    fun workShopStockTake(view: View)            { startWithExtras(WorkshopStockTaking::class.java) }
    fun workShopStockTransfer(view: View)        { startWithExtras(WorkshopPendingVehicle::class.java) }
    fun workShopChassisEnquiry(view: View)       { startWithExtras(WorkShopChassisEnquiry::class.java) }
    fun stockTrfReceive(view: View)              { startWithExtras(WorkShopStockReceiveWithImages::class.java) }
    fun stockTrfMade(view: View)                 { startWithExtras(WorkShopStockTransferWithImages::class.java) }
    fun viewVehicleHistory(view: View)           { startWithExtras(WorkshopVehicleHistory::class.java) }
    fun workshopGatePassEntry(view: View)        { startWithExtras(WorkShopGatepass2::class.java) }
    fun workshopParking(view: View)              { startWithExtras(WorkshopParkingModule::class.java) }
    fun workshopWashingModule(view: View)        { startWithExtras(WorkshopWashingModule::class.java) }
    fun workshopWashingModuleChassis(view: View) { startWithExtras(SalesWashingWithChassis::class.java) }
    fun workshopDemoVehicle(view: View)          { startWithExtras(WorkshopDemoVehicle::class.java) }
    fun workshopDemoVehicleAvailability(view: View) { startWithExtras(WorkshopAvailableDemoVehicle::class.java) }
    fun floorVehicleTracking(view: View)         { startWithExtras(FloorVehicleTracking::class.java) }
    fun workshopWashingGoa(view: View)           { startWithExtras(WorkshopWashingGoa::class.java) }
    fun serviceStockTaking(view: View)           { startWithExtras(ServiceStockTaking::class.java) }
    fun goToVehicleDelivery(view: View)          { startWithExtras(VehicleDelivery::class.java) }
    fun goToDeliveryReports(view: View)          { startWithExtras(DeliveryReport::class.java) }
//    fun generateLogins(view: View)               { startWithExtras(GenerateLogins::class.java) }

    // ── Single extras helper ──────────────────────────────────
    private fun startWithExtras(cls: Class<*>) {
        val intent = Intent(this, cls).apply {
            putExtra("source",           source)
            putExtra("attribute1",       attribute1)
            putExtra("attribute2",       attribute2)
            putExtra("role",      role)
            putExtra("login_name",       login_name)
            putExtra("ouId",             ouId)
            putExtra("location",         location)
            putExtra("locId",            locId)
            putExtra("location_name",    location_name)
            putExtra("deptName",         deptName)
            putExtra("emailId",          emailId)
            putExtra("userContact",      userContact)
            putExtra("userName",         userName)
            putExtra("erpDeptName",      erpDeptName)
            putExtra("deliveryDeptName", deliveryDeptName)
            putExtra("dmsLoc", dmsLoc)
        }
        startActivity(intent)
    }

    private fun logout() {
        SessionManager.clearSession(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun aboutActivity() {
        val intent = Intent(this, AboutActivity::class.java)
        intent.putExtra("deptName", deptName)
        startActivity(intent)
    }
}