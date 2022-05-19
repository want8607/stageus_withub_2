package com.example.withub.mainFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.withub.*
import com.example.withub.mainFragments.mainFragmentAdapters.ExpandableRVAdapter
import kotlinx.coroutines.*

class RankingAreaFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var recyclerView : RecyclerView
    lateinit var expandableAdapter : ExpandableRVAdapter
    var commitApi = RetrofitClient.initRetrofit().create(CommitApi::class.java)
    val handler = CoroutineExceptionHandler{_,exception->
        Log.d("error",exception.toString())
        Log.d("error",exception.cause.toString())
    }
    val rankingDataList : MutableList<ArrayList<RankData>> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.ranking_area_fragment,container,false)
        mainActivity = activity as MainActivity
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //랭킹 리사이클러 설정
        CoroutineScope(Dispatchers.Main).launch(handler) {
            getRankingData()
            expandableAdapter = ExpandableRVAdapter(rankingDataList)
            recyclerView = view.findViewById<RecyclerView>(R.id.ranking_area_recycler_view)
            recyclerView.adapter = expandableAdapter
            recyclerView.setHasFixedSize(true)
        }
        
        //스와이프 리프레시 설정
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.ranking_area_swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch(handler) {
                Log.d("실행","1")
                getRankingData()
                Log.d("실행","2")
                expandableAdapter.refresh(rankingDataList.toMutableList())
                Log.d("실행","3")
                swipeRefreshLayout.isRefreshing = false
                Log.d("실행","4")
                Toast.makeText(mainActivity,"업데이트 완료",Toast.LENGTH_SHORT).show()
            }
        }
    }
    //랭킹데이터 가져오기
    suspend fun getRankingData(){
        withContext(CoroutineScope(Dispatchers.Default).coroutineContext + handler) {
            val getAreaRanking = withContext(Dispatchers.IO) {
                commitApi.getAreaRank(MyApp.prefs.accountToken!!)
            }
            val newDailyRankData = ArrayList<RankData>()
            val newWeeklyRankData = ArrayList<RankData>()
            val newMonthlyRankData = ArrayList<RankData>()
            val newContinuousRankData = ArrayList<RankData>()
            withContext(Dispatchers.Default) {
                for (i in 0..9) {
                    if (i < getAreaRanking.daily_rank.size) {
                        newDailyRankData.add(getAreaRanking.daily_rank[i])
                        newWeeklyRankData.add(getAreaRanking.weekly_rank[i])
                        newMonthlyRankData.add(getAreaRanking.monthly_rank[i])
                        newContinuousRankData.add(getAreaRanking.continuous_rank[i])
                    } else {
                        val emptyRankData = RankData("-", -1)
                        newDailyRankData.add(emptyRankData)
                        newWeeklyRankData.add(emptyRankData)
                        newMonthlyRankData.add(emptyRankData)
                        newContinuousRankData.add(emptyRankData)
                    }
                }
            }
            rankingDataList.clear()
            rankingDataList.add(newDailyRankData)
            rankingDataList.add(newWeeklyRankData)
            rankingDataList.add(newMonthlyRankData)
            rankingDataList.add(newContinuousRankData)
        }
    }
}