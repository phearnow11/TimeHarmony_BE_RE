package com.example.TimeHarmony.service.interfacepack;

import java.util.List;
import java.util.Map;

import com.example.TimeHarmony.entity.Admins;
import com.example.TimeHarmony.entity.AppraiseRequest;
import com.example.TimeHarmony.entity.Members;
import com.example.TimeHarmony.entity.Orders;
import com.example.TimeHarmony.entity.Payment;
import com.example.TimeHarmony.entity.Report;
import com.example.TimeHarmony.entity.Staff;
import com.example.TimeHarmony.entity.Watch;
import com.example.TimeHarmony.enumf.StaffRole;

public interface IAdminService {

  public List<Members> getMembers();

  public List<Admins> getAdmins();

  public List<Watch> getWatches();

  public String deleteMemberbyId(String id);

  public String banMemberbyId(String username);

  public String unbanMemberbyId(String username);

  public List<Map<String, String>> viewWatchCreationHistory();

  public List<Report> viewReports();

  public String toStaff(String id, String username);

  public String addMembers(List<Members> m);

  public String addWatches(List<Watch> w, String s_id) throws Exception;

  public String testUser();

  public float getProfit();

  List<String> getAllShippingOrder();

  String changeStaffRole(String id, StaffRole role);

  public List<Payment> getAllFailOrder();

  String assignAppraiser(String request_id, String aid, String date);

  List<AppraiseRequest> getAllRequest();

  List<Orders> getOrderByState(int state);

  List<Staff> getStaffByRole(StaffRole role);

  String updateAssignAppraiser(String request_id, String aid, String date);

  int orderOfDay(String date);

  int successOrderOfDay(String date);

  long totalAmountOrderOfDay(String date);

  long totalAmountSuccess(String date);

  List<Map<String, Integer>> top3Brand();

  float getWebProfitByDate(String from, String to);

  float getWebProfitByMonth(String fromM, String toM);

  String assignShipper(String oid, String mid);

  int getSuccessOrderByMonth(String month);

  long getTotalAmountSuccessOrderByMonth(String month);

  long getTotalProfitOrderByMonth(String month);

  List<Object> getMemberByState(int state, String role, String staff_role, int page);

  List<Map<String, Long>> getDailyMoneyATM(String startDate, String endDate);

  List<Map<String, Long>> getDailyMoneyCOD(String startDate, String endDate);

  List<Map<String, Long>> getDailyRevenue(String startDate, String endDate);

  List<Map<String, Integer>> getDailyNumOrderSuccess(String startDate, String endDate);

  String deleteWatch(String wid);
}
