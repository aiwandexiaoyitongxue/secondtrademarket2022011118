package com.secondtrade.service;
import com.secondtrade.entity.Order;   
import com.secondtrade.entity.User;
import java.util.List;
import com.secondtrade.entity.UserAddress;
import com.secondtrade.dto.RegisterDTO;
import com.secondtrade.util.FileUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import com.secondtrade.entity.WalletRecord;
public interface UserService {
    void register(RegisterDTO dto);
    User login(String username, String password);

    // 下面这些都要加上
    List<User> getPendingUsers();
    void approveUser(Long id);
    void rejectUser(Long id);
    List<User> getAllUsers();
    void disableUser(Long id);
    void updateUser(User user);
    User getUserById(Long id);
    // 获取用户钱包信息
    User getUserWallet(Long userId);

    // 获取用户订单列表
    List<Order> getUserOrders(Long userId);

    // 根据状态获取用户订单列表
    List<Order> getUserOrdersByStatus(Long userId, String status);

    // 获取用户地址列表
    List<UserAddress> getUserAddresses(Long userId);

    User getUserByUsername(String username);

    void addUserAddress(UserAddress address);
    void updateUserAddress(UserAddress address);
    void deleteUserAddress(Long userId, Long addressId);
    void setDefaultUserAddress(Long userId, Long addressId);
    UserAddress getDefaultAddress(Long userId);
    List<Order> getUserOrders(Long userId, String orderNo, Integer status, String startDate, String endDate);
    int getUserPoints(Long userId);
    // 修改密码
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    // 上传头像
    String uploadAvatar(Long userId, MultipartFile file) throws IOException;
     void updatePoints(Long userId, int points);
    void updateWalletBalance(Long userId, Double balance);

    /**
     * 增加商户用户钱包余额并记录流水
     */
    void addMerchantBalance(Long merchantUserId, Double amount);
    // ...已有方法...
    void recharge(Long userId, Double amount);
    List<WalletRecord> getRechargeRecords(Long userId);
}