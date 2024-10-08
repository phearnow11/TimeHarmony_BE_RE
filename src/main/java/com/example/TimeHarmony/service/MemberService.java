package com.example.TimeHarmony.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.TimeHarmony.dtos.AccessHistory;
import com.example.TimeHarmony.dtos.Favorites;
import com.example.TimeHarmony.entity.Addresses;
import com.example.TimeHarmony.entity.Authorities;
import com.example.TimeHarmony.entity.Members;
import com.example.TimeHarmony.entity.Users;
import com.example.TimeHarmony.enumf.Roles;
import com.example.TimeHarmony.repository.AddressRepository;
import com.example.TimeHarmony.repository.AuthoritiesRepository;
import com.example.TimeHarmony.repository.MemberRepository;
import com.example.TimeHarmony.repository.SellerRepository;
import com.example.TimeHarmony.repository.UsersRepository;
import com.example.TimeHarmony.service.interfacepack.IMemberService;

@Service
public class MemberService implements IMemberService {

  private final byte DEFAULT_ACTIVE_STATUS = 1;
  private final byte DEFAULT_INACTIVE_STATUS = 0;

  @Autowired
  private MemberRepository MEMBER_REPOSITORY;
  @Autowired
  private UsersRepository USER_REPOSOTORY;
  @Autowired
  private AuthoritiesRepository AUTHORITIES_REPOSITORY;
  @Autowired
  private AddressRepository ADDRESS_REPOSITORY;
  @Autowired
  private SellerRepository SELLER_REPOSITORY;
  @Autowired
  private StringService STRING_SERVICE;

  private final PasswordEncoder passwordEncoder;

  public MemberService(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Optional<Members> getMemberbyID(String member_id) {
    if (member_id.isEmpty())
      return null;
    Optional<Members> member = Optional.empty();
    member = MEMBER_REPOSITORY.findById(UUID.fromString(member_id));
    if (member.isPresent()) {
      return member;
    }
    return null;
  }

  @Override
  public List<Addresses> getAddresses(String member_id) {
    try {
      return ADDRESS_REPOSITORY.getAddresses(UUID.fromString(member_id));
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  @Override
  public String deleteMember(String id) {
    try {
      USER_REPOSOTORY.deleteMember(UUID.fromString(id));
      return "Member deleted";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public Members saveUser(Members member, Users logInfo) {
    Authorities auth = new Authorities();
    String cart_id = "C" + STRING_SERVICE.autoGenerateString(11);
    if (MEMBER_REPOSITORY.findAll().isEmpty()) {
      auth = new Authorities(logInfo.getUsername(), Roles.ROLE_ADMIN.name());
    } else
      auth = new Authorities(logInfo.getUsername(), Roles.ROLE_USER.name());
    logInfo.setPassword(passwordEncoder.encode(logInfo.getPassword()));
    USER_REPOSOTORY.save(logInfo);
    AUTHORITIES_REPOSITORY.save(auth);
    member.setCart_id(cart_id);
    return MEMBER_REPOSITORY.save(member);
  }

  @Override
  public boolean isExist(Users user, String email) {
    boolean res = USER_REPOSOTORY.existsById(user.getUsername());
    List<Members> members = MEMBER_REPOSITORY.getMemberbyEmail(email);
    if (members.isEmpty() && !res)
      return false;
    return true;
  }

  @Override
  public Members getMemberbyUserLogInfo(Users userLogInfo) {
    Optional<Members> member = MEMBER_REPOSITORY.getMemberbyUserLogInfo(userLogInfo);
    if (member.isPresent())
      return member.get();
    return null;
  }

  @Override
  public Users getUserbyUsername(String username) {
    Optional<Users> user = Optional.empty();
    user = USER_REPOSOTORY.getUserbyUsername(username);
    if (user.isPresent())
      return user.get();
    return null;
  }

  @Override
  public void login(String id) {
    MEMBER_REPOSITORY.updateLastLoginDate(Timestamp.valueOf(LocalDateTime.now()), UUID.fromString(id));
    MEMBER_REPOSITORY.updateActiveStatus(DEFAULT_ACTIVE_STATUS, UUID.fromString(id));
  }

  @Override
  public void logout(String id) {
    MEMBER_REPOSITORY.logoutRepo(DEFAULT_INACTIVE_STATUS, UUID.fromString(id),
        Timestamp.valueOf(LocalDateTime.now()));
  }

  @Override
  public String changeUserPassword(String username, String new_password) {
    try {
      USER_REPOSOTORY.updateUserPassword(passwordEncoder.encode(new_password), username);
      return "Password changed";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public String updateEmail(String member_id, String new_email) {
    try {
      if (MEMBER_REPOSITORY.getMemberByEmail(new_email).isPresent())
        throw new Exception("Email has already been used");
      MEMBER_REPOSITORY.updateMemberEmail(new_email, UUID.fromString(member_id));
      MEMBER_REPOSITORY.removeVerify(UUID.fromString(member_id));
      return new_email;
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public List<AccessHistory> getAllAccessHistories(String member_id) {
    return MEMBER_REPOSITORY.getAllAccessHistoriesFromMember(UUID.fromString(member_id));
  }

  @Override
  public String updateAccessHistories(String member_id, List<String> urls, List<Timestamp> times) {
    try {
      for (int i = 0; i < urls.size(); i++) {
        MEMBER_REPOSITORY.insertAccessHistory(UUID.fromString(member_id), urls.get(i), times.get(i));
      }
      return "Access History updated !";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public Addresses addAddress(Addresses address) {
    String a_id = "A" + STRING_SERVICE.autoGenerateString(11);
    address.setAddress_id(a_id);
    if (address.is_default()) {
      Optional<Addresses> ad = ADDRESS_REPOSITORY.checkDefault(address.getMember().getMember_id(), true);
      if (ad.isPresent()) {
        ADDRESS_REPOSITORY.updateDefault(false, ad.get().getAddress_id());
      }
    }
    return ADDRESS_REPOSITORY.save(address);
  }

  @Override
  public String toSeller(String m_id, String username) {
    try {
      SELLER_REPOSITORY.toSeller(UUID.fromString(m_id));
      AUTHORITIES_REPOSITORY.updateRole(Roles.ROLE_SELLER.name(), username);
    } catch (Exception e) {
      return "Seller is already existed";
    }
    return "Member to Seller Success";
  }

  @Override
  public String addFavorites(String m_id, String wid) {
    try {
      Optional<Favorites> exist_fa = MEMBER_REPOSITORY.getSpecificFavorite(UUID.fromString(m_id), wid);
      if (exist_fa.isPresent())
        throw new Exception("Favorites is already exist");
      MEMBER_REPOSITORY.insertFavorites(UUID.fromString(m_id), wid);
      return "Favorites added";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public String deleteFavorites(String m_id, String w_ids) {
    try {
      MEMBER_REPOSITORY.deleteFavorites(w_ids, UUID.fromString(m_id));
      return "Favorites deleted";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public List<Favorites> getFavoritesFromMember(String m_id) {
    int GET_FAVORITES_LIMIT = 10;
    try {
      return MEMBER_REPOSITORY.getFavoritesFromMember(UUID.fromString(m_id), Limit.of(GET_FAVORITES_LIMIT));
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  @Override
  public String deleteAddress(String m_id, String a_id) {
    try {
      ADDRESS_REPOSITORY.deleteAddress(UUID.fromString(m_id), a_id);
      return "Address deleted";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public Addresses getDefaultAddress(String m_id) {
    try {
      Optional<Addresses> add = ADDRESS_REPOSITORY.checkDefault(UUID.fromString(m_id), true);
      if (add.isPresent())
        return add.get();
      return null;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  @Override
  public String updateMemberImage(String id, String url) {
    try {
      MEMBER_REPOSITORY.updateMemberImage(url, UUID.fromString(id));
      return "Image updated";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public Addresses getAddressByAddressId(String addrId) {
    try {
      return ADDRESS_REPOSITORY.findById(addrId).get();
    } catch (Exception e) {
      System.out.println(e.toString());
      return null;
    }
  }

  @Override
  public String checkPassword(String username, String rawpassword) {
    try {
      String password = MEMBER_REPOSITORY.getPassword(username);
      if (password != null) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if (bcrypt.matches(rawpassword, password))
          return "correct password";
        return "incorrect password";
      }
      return "User not found";
    } catch (Exception e) {
      return e.toString();
    }
  }

  @Override
  public Members getMemberbyEmail(String email) {
    Optional<Members> member = MEMBER_REPOSITORY.getMemberByEmail(email);
    if (member.isPresent())
      return member.get();
    return null;
  }

  @Override
  public Boolean checkUserEnabled(String username) {
    return USER_REPOSOTORY.getEnable(username) == 1;
  }

  @Override
  public String updateMember(String id, Map<String, Object> data) {
    try {
      Members cur_member = getMemberbyID(id).get();
      if (data.get("verify_email") != null) {
        cur_member.setGoogle_id(data.get("verify_email").toString());
      }
      if (data.get("username") != null) {
        USER_REPOSOTORY.updateUsername(data.get("username").toString(),
            cur_member.getUser_log_info().getUsername());
      }
      if (data.get("email") != null) {
        if (!MEMBER_REPOSITORY.getMemberByEmail(data.get("email").toString()).isEmpty())
          throw new Exception("Email has already existed");
        cur_member.setEmail(data.get("email").toString());
      }
      cur_member.setFirst_name(
          data.get("fname") == null ? cur_member.getFirst_name() : data.get("fname").toString());
      cur_member.setLast_name(
          data.get("lname") == null ? cur_member.getFirst_name() : data.get("lname").toString());
      cur_member.setPhone(data.get("phone") == null ? cur_member.getFirst_name() : data.get("phone").toString());
      MEMBER_REPOSITORY.save(cur_member);
      return "Member updated";
    } catch (Exception e) {
      return e.toString();
    }

  }

}
