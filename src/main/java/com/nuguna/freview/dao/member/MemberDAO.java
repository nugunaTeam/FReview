package com.nuguna.freview.dao.member;

import static com.nuguna.freview.util.DbUtil.closeResource;
import static com.nuguna.freview.util.DbUtil.getConnection;

import com.nuguna.freview.dto.MemberRecommendationInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberDAO {

  public List<MemberRecommendationInfo> selectMemberInfo(String gubun) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    List<MemberRecommendationInfo> list = new ArrayList<>();

    String sql = "SELECT " +
        "    m.id, " +
        "    m.nickname, " +
        "    m.profile_photo_url, " +
        "    GROUP_CONCAT(DISTINCT ft.name) AS food_types, " +
        "    GROUP_CONCAT(DISTINCT t.name) AS tags " +
        "FROM " +
        "    member m " +
        "LEFT JOIN " +
        "    member_food_type mft ON m.member_seq = mft.member_seq " +
        "LEFT JOIN " +
        "    food_type ft ON mft.food_type_seq = ft.food_type_seq " +
        "LEFT JOIN " +
        "    member_tag mt ON m.member_seq = mt.member_seq " +
        "LEFT JOIN " +
        "    tag t ON mt.tag_seq = t.tag_seq " +
        "WHERE " +
        "    m.gubun = ? " +
        "GROUP BY " +
        "    m.nickname, m.profile_photo_url;";

    try {
      conn = getConnection();
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, gubun);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        MemberRecommendationInfo boss = new MemberRecommendationInfo();
        boss.setId(rs.getString("id"));
        boss.setNickname(rs.getString("nickname"));
        boss.setProfilePhotoUrl(rs.getString("profile_photo_url"));
        boss.setFoodTypes(rs.getString("food_types"));
        boss.setTags(rs.getString("tags"));

        list.add(boss);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      closeResource(pstmt, conn, rs);
    }

    return list;
  }
}
