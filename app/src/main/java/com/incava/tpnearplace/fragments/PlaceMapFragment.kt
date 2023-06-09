package com.incava.tpnearplace.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.incava.tpnearplace.activities.MainActivity
import com.incava.tpnearplace.activities.PlaceUrlActivity
import com.incava.tpnearplace.databinding.FragmentPlaceMapBinding
import com.incava.tpnearplace.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.POIItemEventListener


class PlaceMapFragment : Fragment() {
    private lateinit var binding: FragmentPlaceMapBinding
    val mapView: MapView by lazy { MapView(context) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceMapBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.containerMapview.addView(mapView)

        // 마커 or 말풍선 클릭시 이벤트
        mapView.setPOIItemEventListener(markerEventListener)


        //지도관련 설정 (지도 위치, 마커추가 등)
        setMapAndMarkers()
    }

    private fun setMapAndMarkers() {
        // 맵 중심점 변경.
        // 현재 내 위치 위도, 경도 좌표
        var lat: Double = (requireActivity() as MainActivity).myLocation?.latitude ?: 37.5663
        var lng: Double = (requireActivity() as MainActivity).myLocation?.longitude ?: 126.9779
        var myMapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)
        mapView.apply {
            setMapCenterPointAndZoomLevel(myMapPoint, 5, true)
            zoomIn(true)
            zoomOut(true)

            // 내 위치 표시 마커 추가.

            var marker = MapPOIItem()
            marker.apply {
                itemName = "ME"
                mapPoint = myMapPoint
                markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.YellowPin
                //userObject를 맞춰줘야하므로 넣어주기.
                userObject = Place("","","","","","",lng.toString(),lat.toString(),"","")
            }
            mapView.addPOIItem(marker)

            val documents: MutableList<Place>? =
                (activity as MainActivity).searchPlaceResponse?.documents
            documents?.forEach {
                val point: MapPoint =
                    MapPoint.mapPointWithGeoCoord(it.y.toDouble(), it.x.toDouble())

                val marker = MapPOIItem().apply {
                    mapPoint = point
                    itemName = it.place_name
                    markerType = MapPOIItem.MarkerType.RedPin
                    selectedMarkerType = MapPOIItem.MarkerType.YellowPin
                    // 마커객체에 보관하고 싶은 데이터가 있다면, 해당 마커에 관련된 정보를 저장하고 있는 객체를 마커에 저장해놓기.
                    userObject = it
                }
                mapView.addPOIItem(marker)
            }
        }
    }

    // 마커 or 말풍선 클릭 이벤트 리스너
    val markerEventListener: POIItemEventListener = object : POIItemEventListener {
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
            //마커를 클릭했을 때 발동!<- 여기 부분이 누르면 그 좌표가 중심점으로 이동.
            var place = (p1?.userObject as Place)
            mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    place.y.toDouble(),
                    place.x.toDouble()
                ), true
            )
        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            //deprecated... 아래 메서드로 대체
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            //말풍선 터치했을 때 발동.
            //두번째 파라미터 p1 : 클릭한 마커의 객체.
            p1?.userObject ?: return
            val place = (p1.userObject as Place)
            if (place.place_url == "") return // url이 없다면 return
            val intent = Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url)
            startActivity(intent)
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            //마커를 드래그했을때 발동.
        }
    }


}