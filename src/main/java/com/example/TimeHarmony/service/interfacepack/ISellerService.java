package com.example.TimeHarmony.service.interfacepack;

import java.util.List;
import java.util.Map;

import com.example.TimeHarmony.entity.AppraiseRequest;
import com.example.TimeHarmony.entity.Sellers;
import com.example.TimeHarmony.entity.Users;
import com.example.TimeHarmony.entity.Watch;

public interface ISellerService {

  public Sellers saveSeller(Sellers seller, Users logInfo);

  public String createWatch(Watch watch, Sellers seller);

  public String deleteWatchById(String watch_id);

  public Watch updateWatchByFields(Map<String, String> data, Watch existingWatch);

  public Watch updateWatch(Watch newWatch, Watch existingWatch);

  public List<Watch> findAllWatchBySeller(String sid);

  public Sellers getSellerbyId(String id);

  String confirmShipping(String wid, String oid);

  List<Watch> getWaitingList(String mid);

  String confirmOrder(String oid);

  String setRate(float incoming_rate, String sid, String rater);

  Float getRate(String sid);

  public float getTotalProfitBySeller(String sid);

  public float getProfitByMonth(int month, int year, String sid);

  public float getProfitByDay(String date, String sid);

  List<String[]> getOrderFromWatch(String wid);

  public int countSellWatch(String sid);

  public int countSoldWatch(String sid);

  String createAppraiseRequest(String sid, Map<String, Object> data);

  List<AppraiseRequest> getMyRequest(String sid);

  List<Map<String, Long>> getDailyProfit(String sid, String startDate, String endDate); 
}
