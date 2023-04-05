package com.incava.tpnearplace.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivityMainBinding
import com.incava.tpnearplace.fragments.PlaceListFragment
import com.incava.tpnearplace.fragments.PlaceMapFragment
import com.incava.tpnearplace.model.KakaoSearchPlaceResponse
import com.incava.tpnearplace.network.RetrofitApiService
import com.incava.tpnearplace.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var searchQuery: String = "화장실"
    var myLocation: Location? = null

    // [ Google Fused Location API 사용 : play-services-location ]
    val providerClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    // 검색결과 응답객체 참조변수
    var searchPlaceResponse: KakaoSearchPlaceResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바를 제목줄로 설정.
        setSupportActionBar(binding.toolbar)

        //처음 보여질 프래그먼트 동적 추가
        supportFragmentManager.beginTransaction().add(R.id.container_fragment, PlaceListFragment())
            .commit()

        // 탭 레이아웃의 탭버튼 클릭시에 보여질 프래그먼트 설정
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "List" -> supportFragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, PlaceListFragment()).commit()

                    "Map" -> supportFragmentManager.beginTransaction()
                        .replace(R.id.container_fragment, PlaceMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        // 소프트키보드의 검색버튼을 클릭하였을때
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            searchQuery = binding.etSearch.text.toString()
            //카카오 검색API를 이용해 장소들 검색하기
            searchPlace()
            false
        }
        //특정 키워드 단축 검색 버튼들이 리스너 처리하는 함수 호출.
        setChoiceButtonsListener()

        // 내 위치 정보 제공에 대한 동적  퍼미션 요청
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestMyLocation()
        }
    }// onCreate

    //
    val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        object : ActivityResultCallback<Boolean> {
            override fun onActivityResult(result: Boolean?) {
                if (result!!) requestMyLocation()
                else Toast.makeText(
                    this@MainActivity,
                    "위치 정보 제공이 필요합니다 검색이 제한됩니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


    //내 위치 요청 작업 메서드.
    fun requestMyLocation() {
        val request: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        //실시간 위치정보 갱신 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            myLocation = p0.lastLocation

            //정보 얻어왔으니 실시간 업데이트 종료.
            providerClient.removeLocationUpdates(this)

            //위치 얻었으니 검색 시작
            searchPlace()

        }
    }


    //카카오 장소 검색 API를 파싱하는 작업메서드
    private fun searchPlace() {
        //Toast.makeText(this, "$searchQuery - ${myLocation?.latitude} , ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()
        //kakao keyword place search api... REST API작업 - Retrofit
        val retrofit: Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com/")
        val retrofitApiService = retrofit.create(RetrofitApiService::class.java).searchPlace(
            searchQuery,
            myLocation?.latitude.toString(),
            myLocation?.longitude.toString()
        )
        retrofitApiService.enqueue(object : Callback<KakaoSearchPlaceResponse> {
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                searchPlaceResponse = response.body()
                //Toast.makeText(this@MainActivity, "${searchPlaceResponse?.meta?.total_count}", Toast.LENGTH_SHORT).show()
                //만들어 지면 뷰를 다시 만들어 보여주도록.
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceListFragment()).commit()

                //탭버튼의 위치를 ListFragment tab으로 변경
                binding.tabLayout.getTabAt(0)?.select()
            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setChoiceButtonsListener() {
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceTaxi.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceSubway.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceParking.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
    }

    private var choiceID = R.id.choiceWc
    private fun clickChoice(view: View) {
        //현재 클릭된 버튼의 배경을 회색 원그림으로 변경
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)
        view.setBackgroundResource(R.drawable.bg_choice_select)
        choiceID = view.id

        //초이스된 것에 따라 검색장소명을 변경하여 다시 검색
        when (choiceID) {
            R.id.choiceWc -> searchQuery = "화장실"
            R.id.choiceMv -> searchQuery = "영화관"
            R.id.choiceTaxi -> searchQuery = "택시"
            R.id.choiceSubway -> searchQuery = "지하철"
            R.id.choiceParking -> searchQuery = "주차장"
            R.id.choiceEv -> searchQuery = "전기충전소"
            R.id.choiceGas -> searchQuery = "주유소"
            R.id.choicePark -> searchQuery = "공원"
        }
        searchPlace()
        binding.etSearch.text.clear()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_aa -> Toast.makeText(this, "aa", Toast.LENGTH_SHORT).show()
            R.id.menu_bb -> Toast.makeText(this, "bb", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

}