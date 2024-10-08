package com.example.TimeHarmony.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.TimeHarmony.dtos.WatchImages;
import com.example.TimeHarmony.entity.Watch;

import jakarta.transaction.Transactional;

public interface WatchRepository extends JpaRepository<Watch, String> {
  @Query("select w from Watch w where w.gender = ?1 and w.state = 1")
  List<Watch> findWatchesByGender(String gender);

  @Query("select w from Watch w where w.series = ?1 and w.state = 1")
  List<Watch> findWatchesBySeries(String series);

  @Query("select w from Watch w where w.brand = ?1 and w.state = 1")
  List<Watch> findWatchesByBrand(String brand);

  @Query("select w from Watch w where w.style_type = ?1 and w.state = 1")
  List<Watch> findWatchesByStyle(String style);

  @Query("select w from Watch w where w.dial_color = ?1 and w.state = 1")
  List<Watch> findWatchesByDialColor(String dial_color);

  @Query("select w from Watch w where w.case_shape = ?1 and w.state = 1")
  List<Watch> findWatchesByCaseShape(String caseshape);

  @Query("select w from Watch w where w.band_type = ?1 and w.state = 1")
  List<Watch> findWatchesByBandType(String bandtype);

  @Query("select w from Watch w where w.movement = ?1 and w.state = 1")
  List<Watch> findWatchesByMovement(String movement);

  @Query(value = "select * from [dbo].[Watch] join [dbo].[Favorites] on [dbo].[Watch].watch_id = [dbo].[Favorites].watch_id where [dbo].[Favorites].member_id = :mid and state = 1 ", nativeQuery = true)
  List<Watch> findWatchesByFavorites(@Param("mid") UUID mid);

  @Query(value = "select top (30) * from Watch where state = 1 order by watch_create_date desc ", nativeQuery = true)
  List<Watch> find30watchesByDESCDate();

  @Query(value = "select top (60) * from Watch where state = 1 order by watch_create_date desc ", nativeQuery = true)
  List<Watch> get1pageOfWatchByDESCDate();

  @Query(value = "select * from Watch where state = 1 order by watch_create_date desc OFFSET :start rows fetch next 18 rows only", nativeQuery = true)
  List<Watch> findNext18WatchesbyDESCDate(@Param("start") int start);

  @Query(value = "select * from Watch where state = 1 order by watch_create_date desc OFFSET :start rows fetch next 60 rows only", nativeQuery = true)
  List<Watch> findNextPageDESCDate(@Param("start") int start);

  @Query(value = "select * from Watch where feature like %:start% and state = 1", nativeQuery = true)
  List<Watch> findWatchesByFeatures(@Param("start") String feature);

  @Query(value = "select * from Watch where price BETWEEN :start AND :end and state = 1", nativeQuery = true)
  List<Watch> findWatchByRangePrice(@Param("start") float start, @Param("end") float end);

  @Modifying
  @Transactional
  @Query(value = "insert into Watch_images(watch_id, image_url) values (:id, :url)", nativeQuery = true)
  void saveWatch_Images(@Param("id") String id, @Param("url") String url);

  @Query("select w from Watch w where  w.state = :state")
  List<Watch> getWatchesByState(@Param("state") int state);

  @Modifying
  @Transactional
  @Query(value = "delete Watch_Images where watch_id = :id and image_url = :url", nativeQuery = true)
  void deleteImage(@Param("id") String id, @Param("url") String url);

  List<Watch> findAllByState(byte state);

  @Query(value = "select * from Watch where freetext(Watch.*, :key) AND state = 1", nativeQuery = true)
  List<Watch> findByKeyWord(@Param("key") String key);

  @Query(value = "select * from Watch where (watch_name like %:key% or watch_description like %:key%) and state = 1 ", nativeQuery = true)
  List<Watch> findByKeyIfFullTextNull(@Param("key") String key);

  @Query(value = "select COUNT(watch_id) as watch_num from Watch", nativeQuery = true)
  Integer getWatchNum();

  @Query(value = "select w from Watch w where (:gender is null or w.gender like :gender) and (:series is null or w.series like :series) and (:brand is null or w.brand like :brand) and (:style is null or w.style_type like :style) and (:feature is null or w.feature like :feature) and w.price > :lprice and w.price < :hprice and w.state = 1 and w.watch_name like %:key%")
  List<Watch> getWatchesByFilter(@Param("key") String key, @Param("gender") String gender,
      @Param("series") String series,
      @Param("brand") String brand,
      @Param("style") String style, @Param("feature") String feature, @Param("lprice") float lowprice,
      @Param("hprice") float hprice, Pageable pageable);

  @Query(value = "select * from [dbo].[Watch_images] where watch_id = :wid", nativeQuery = true)
  List<WatchImages> getWatchImages(@Param("wid") String wid);

  @Query(value = "select COUNT(watch_id) as watch_num from Watch w where (:gender is null or w.gender like :gender) and (:series is null or w.series like :series) and (:brand is null or w.brand like :brand) and (:style is null or w.style_type like :style) and (:feature is null or w.feature like :feature) and w.price > :lprice and w.price < :hprice and w.state = 1")
  Integer getWatchNumWithConditions(@Param("gender") String gender, @Param("series") String series,
      @Param("brand") String brand,
      @Param("style") String style, @Param("feature") String feature, @Param("lprice") float lowprice,
      @Param("hprice") float hprice);

  @Modifying
  @Transactional
  @Query(value = "update Watch set watch_approval_date = :date, state = :state where watch_id = :id", nativeQuery = true)
  void approveWatch(@Param("date") Timestamp date, @Param("id") String wid, @Param("state") byte steate);

  @Query(value = "select * from Watch where member_id = :mid and (state = 1 or state = 3)", nativeQuery = true)
  List<Watch> getWatchesBySeller(@Param("mid") UUID mid, Limit limit);

  @Query(value = "select * from Watch where member_id = :mid and (state = 3 or state = 4 or state = 6 or state = 7)", nativeQuery = true)
  List<Watch> getWaitingWatches(@Param("mid") UUID mid, Limit limit);

  @Modifying
  @Transactional
  @Query(value = "update Watch set state = :state where watch_id = :id", nativeQuery = true)
  void updateWatchState(@Param("state") byte state, String id);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set [dbo].[Watch].state = 4 from [dbo].[Watch] join [dbo].[Watches_In_Cart] on [dbo].[Watches_In_Cart].watch_id = [dbo].[Watch].watch_id where order_id = :oid ", nativeQuery = true)
  void confirmOrder(@Param("oid") String oid);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set [dbo].[Watch].state = 1 from [dbo].[Watch] join [dbo].[Watches_In_Cart] on [dbo].[Watches_In_Cart].watch_id = [dbo].[Watch].watch_id where order_id = :oid and [dbo].[Watch].state = 3", nativeQuery = true)
  void cancelOrder(@Param("oid") String oid);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set [dbo].[Watch].state = 7 from [dbo].[Watch] join [dbo].[Watches_In_Cart] on [dbo].[Watches_In_Cart].watch_id = [dbo].[Watch].watch_id where order_id = :oid ", nativeQuery = true)
  void shippedOrder(@Param("oid") String oid);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set state = 6, sold_date = getdate() from [dbo].[Watch] join [dbo].[Watches_In_Cart] on [dbo].[Watches_In_Cart].watch_id = [dbo].[Watch].watch_id where order_id = :oid ", nativeQuery = true)
  void orderSucess(@Param("oid") String oid);

  @Query(value = "select count([dbo].[Watches_In_Cart].watch_id) from [dbo].[Watches_In_Cart] join [dbo].[Watch] on [dbo].[Watch].watch_id = [dbo].[Watches_In_Cart].watch_id where [dbo].[Watches_In_Cart].order_id = :oid and [dbo].[Watch].state <> 4", nativeQuery = true)
  int getWatchNOTShipped(@Param("oid") String oid);

  @Query(value = "select order_id from [dbo].[Watches_In_Cart] where watch_id = :wid", nativeQuery = true)
  List<String> getOrderFromWatch(@Param("wid") String wid);

  @Query(value = "select state from [dbo].[Watch] where watch_id = :wid", nativeQuery = true)
  Integer getState(@Param("wid") String wid);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watches_In_Cart] set state = :state where watch_id in :wids", nativeQuery = true)
  void updateWatchInCartByID(@Param("wids") List<String> wids, @Param("state") int state);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set state = :state from [dbo].[Watch] join [dbo].[Members] on [dbo].[Members].member_id = [dbo].[Watch].member_id where username = :username and (state <= 3 or state = 5)", nativeQuery = true)
  void adminOperationWithAllWatchFromSeller(@Param("username") String username, @Param("state") int state);

  @Modifying
  @Transactional
  @Query(value = "select * from [dbo].[Watch] join [dbo].[Watches_In_Cart] on [dbo].[Watch].watch_id = [dbo].[Watches_In_Cart].watch_id join [dbo].[Orders] on [dbo].[Watches_In_Cart].order_id = [dbo].[Orders].order_id where MONTH(received_date) = :month and [dbo].[Watch].member_id = :sid", nativeQuery = true)
  List<Watch> getWatchSelledByMonth(@Param("month") int month, @Param("sid") String sid);

  @Modifying
  @Transactional
  @Query(value = "select * from Watch where member_id = :sid and (state = 1 or state = 3 or state = 4 or state = 5 or state = 6 or state = 7)", nativeQuery = true)
  List<Watch> getSellerWatches(@Param("sid") UUID sid);

  @Modifying
  @Transactional
  @Query(value = "select * from Watch where member_id = :sid and state = 6", nativeQuery = true)
  List<Watch> getSellerSoldWatches(@Param("sid") UUID sid);

  @Modifying
  @Transactional
  @Query(value = "select * from [dbo].[Watch] where MONTH(sold_date) = :month and YEAR(sold_date) = :year and member_id = :mid and state = 6", nativeQuery = true)
  List<Watch> getWatchSoldByMonth(@Param("month") int month, @Param("year") int year, @Param("mid") UUID mid);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set sold_date = getdate() where watch_id = :wid", nativeQuery = true)
  void saveSoldDate(@Param("wid") String wid);

  @Modifying
  @Transactional
  @Query(value = "select * from [dbo].[Watch] where FORMAT(sold_date, 'yyyy-MM-dd') = :date and member_id = :mid and state = 6", nativeQuery = true)
  List<Watch> getWatchSoldByDate(@Param("date") String date, @Param("mid") UUID mid);

  @Modifying
  @Transactional
  @Query(value = "update [dbo].[Watch] set state = 2 where watch_id = :wid and member_id = :mid", nativeQuery = true)
  void deleteWatch(@Param("wid") String wid, @Param("mid") UUID mid);

  @Query(value = "SELECT top(3) brand, COUNT(*) FROM watch where (state <> 0 and state <> 2) and brand is not null and brand <> '' GROUP BY brand ORDER BY COUNT(*) DESC", nativeQuery = true)
  List<Map<String, Integer>> top3brand();

  @Query(value = "select sold_date as date, sum(price) as daily_profit from [dbo].[Watch] where sold_date between :startDate and :endDate and state = 6 and member_id = :mid and price <> 0 group by sold_date order by sold_date", nativeQuery = true)
  List<Map<String, Long>> getDailyProfit(@Param("mid") UUID mid, @Param("startDate") String startDate,
      @Param("endDate") String endDate);

}
