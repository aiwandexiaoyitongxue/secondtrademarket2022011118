package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.dao.UserDao;
import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.secondtrade.entity.Order;
import com.secondtrade.entity.UserAddress;  
import com.secondtrade.dao.UserAddressDao;
import com.secondtrade.dao.OrderDao;
import com.secondtrade.dao.MerchantDao;
import com.secondtrade.entity.Merchant;
import com.secondtrade.dto.RegisterDTO;
import com.secondtrade.util.FileUtil;
import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;import org.springframework.beans.factory.annotation.Value;             

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    private MerchantDao merchantDao;
     private final UserDao userDao;
     private final PasswordEncoder passwordEncoder;
     private final UserAddressDao userAddressDao;
     private final OrderDao orderDao;
    @Value("${upload.path}")
    private String baseUploadDir;
    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, UserAddressDao userAddressDao, OrderDao orderDao) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.userAddressDao = userAddressDao;
        this.orderDao = orderDao;

    }
   @Override
@Transactional
public void register(RegisterDTO dto) {
    // 1. 唯一性校验
    if (userDao.existsByUsername(dto.getUsername())) throw new RuntimeException("用户名已存在");
    if (userDao.existsByPhone(dto.getPhone())) throw new RuntimeException("手机号已被注册");
    if (dto.getEmail() != null && userDao.existsByEmail(dto.getEmail())) throw new RuntimeException("邮箱已被注册");

    // 2. 构造 User
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRealName(dto.getRealName() != null ? dto.getRealName() : dto.getUsername());
    user.setPhone(dto.getPhone());
    user.setEmail(dto.getEmail());
    user.setCity(dto.getCity());
    user.setGender(dto.getGender());
    user.setBankAccount(dto.getBankAccount());
    user.setStatus(0); // 待审核
    user.setWalletBalance(0.0);
    user.setPoints(0);
    user.setCreatedTime(LocalDateTime.now());
    user.setUpdatedTime(LocalDateTime.now());
    user.setDeleted(0);

    Integer role = dto.getRole();
    System.out.println("注册时 role: " + role); // 打印 role 的值
    if (role == null) role = 0;

    if (role == 0) {
        // 普通用户注册
        user.setRole(0);
        save(user);
    } else if (role == 1) {
        // 商家注册
        user.setRole(1);
        save(user);
        Merchant merchant = new Merchant();
        merchant.setUserId(user.getId());
        merchant.setBusinessLicense(dto.getBusinessLicense());
        merchant.setIdCard(dto.getIdCard());
        merchant.setName(dto.getMerchantName() != null ? dto.getMerchantName() : user.getRealName());
        merchant.setDescription(dto.getDescription());
        merchant.setStatus(0); // 待审核
        merchant.setLevel(1);
        merchant.setSatisfactionRate(new BigDecimal("5.00"));
        merchant.setTotalSales(new BigDecimal("0.00"));
        merchant.setCreatedTime(LocalDateTime.now());
        merchant.setUpdatedTime(LocalDateTime.now());
        merchant.setDeleted(0);
        merchantDao.insertMerchant(merchant);
    } else if (role == 2) {
        // 管理员注册
        user.setRole(2);
        save(user);
        // TODO: 管理员表插入逻辑
        // adminDao.insertAdminInfo(user.getId(), ...);
    } else {
        throw new IllegalArgumentException("不支持的用户类型");
    }
}

    @Override
    public User login(String username, String password) {
        // 1. 查找用户
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户状态
        if (user.getStatus() == 2) {
            throw new RuntimeException("账号已被禁用");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号待审核");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Override
    public List<User> getPendingUsers() {
        return userDao.selectPendingUsers();
    }

    @Override
    @Transactional
    public void approveUser(Long id) {
        User user = userDao.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != 0) {
            throw new RuntimeException("用户状态不正确");
        }
        userDao.approveUser(id);
    }

    @Override
    @Transactional
    public void rejectUser(Long id) {
        User user = userDao.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != 0) {
            throw new RuntimeException("用户状态不正确");
        }
        userDao.rejectUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.selectAllUsers();
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        User user = userDao.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 2) {
            throw new RuntimeException("用户已被禁用");
        }
        userDao.disableUser(id);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userDao.selectUserById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否重复
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userDao.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否重复
        if (!existingUser.getPhone().equals(user.getPhone()) && 
            userDao.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已被使用");
        }
        
        // 检查邮箱是否重复
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail()) && 
            userDao.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        user.setUpdatedTime(LocalDateTime.now());
        userDao.updateUser(user);
    }

    @Override
    public User getUserById(Long id) {
        User user = userDao.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
     @Override
    public User getUserWallet(Long userId) {
        User user = userDao.getUserWallet(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        return userDao.getUserOrders(userId);
    }

    @Override
    public List<Order> getUserOrdersByStatus(Long userId, String status) {
        return userDao.getUserOrdersByStatus(userId, status);
    }


    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressDao.selectByUserId(userId);
    }

    @Override
    public void addUserAddress(UserAddress address) {
        // 如果是第一个地址，设为默认
        if (userAddressDao.countByUserId(address.getUserId()) == 0) {
            address.setIsDefault(1);  // 使用1表示默认地址
        } else {
            address.setIsDefault(0);  // 使用0表示非默认地址
        }
        userAddressDao.insert(address);
    }

    @Override
    public void updateUserAddress(UserAddress address) {
        userAddressDao.update(address);
    }

    @Override
    public void deleteUserAddress(Long userId, Long addressId) {
        userAddressDao.deleteByUserIdAndId(userId, addressId);
    }

    @Override
    public void setDefaultUserAddress(Long userId, Long addressId) {
        userAddressDao.clearDefault(userId);
        userAddressDao.setDefault(userId, addressId);
    }
    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return userAddressDao.selectDefaultByUserId(userId);
    }
    @Override
    public List<Order> getUserOrders(Long userId, String orderNo, Integer status, String startDate, String endDate) {
        // 这里调用 DAO 层，写动态 SQL 查询订单
        return orderDao.selectUserOrders(userId, orderNo, status, startDate, endDate);
    }
    @Override
    public int getUserPoints(Long userId) {
        return userDao.getPointsByUserId(userId);
    }
    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 获取用户
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 3. 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedTime(LocalDateTime.now());
        userDao.updateUser(user);
    }

    @Override
    @Transactional
    public String uploadAvatar(Long userId, MultipartFile avatarFile) throws IOException {
        String avatarUrlFragment = FileUtil.uploadFile(avatarFile, baseUploadDir, "avatar");

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setAvatar(avatarUrlFragment);
        updateById(user);

        return avatarUrlFragment;
    }

    @Override
    public void updatePoints(Long userId, int points) {
        userDao.updatePoints(userId, points);
    }

@Override
public void updateWalletBalance(Long userId, Double balance) {
    userDao.updateWalletBalance(userId, balance);
}
}