package com.gsoft.portal.system.personnel.entity;

import com.gsoft.cos3.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.util.Date;

/**
 * 业务用户
 */
@Entity
@Table(name = "COS_SYS_BUSINESS_USER")
public class BusinessUserEntity extends BaseEntity {
    private static final long serialVersionUID = -364249724627372468L;

    /**
     * 作为用户的唯一标识，用户移动至另一个行政区划下时，就是用这个唯一标识联系新旧用户
     */
    @Column(name = "C_UUID", length = 50)
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    private String uuid;

    /**
     * 中文名
     */
    @Column(name = "C_NAME", length = 50)
    private String name;
    /**
     * 登录名
     */
    @Column(name = "C_LOGIN_NAME", length = 32)
    private String loginName;
    /**
     * 密码
     */
    @Column(name = "C_PASSWORD", length = 50)
    private String passWord;

    /**
     * 移动电话
     */
    @Column(name = "C_MOBILE_PHONE", length = 11)
    private String mobilePhone;

    /**
     * 电子邮箱
     */
    @Column(name = "C_EMAIL", length = 50)
    private String email;

    /**
     * 人员可用状态 :1启用，0停用
     *  默认为1
     */
    @Column(name = "C_STATUS")
    private Boolean status=true;

    /**
     * 密码策略 1=初始化密码，2=短信随机产生密码
     */
    @Column(name = "C_PASSWORD_POLICY")
    private Integer passwordPolicy = 1;

    /**
     * 上次正常登录时间
     */
    @Column(name = "C_LAST_LOGIN")
    private Date lastLogin;
    /**
     * 上次失败时间登录
     */
    @Column(name = "C_LAST_LOGIN_FAILED")
    private Date lastLoginFailed;

    /**
     * 上次登录 IP
     */
    @Column(name = "C_LAST_LOGIN_CLIENT_IP",length=50)
    private String lastLoginClientIp;
    /**
     * 是否允许移动访问 : 1允许，0不允许
     * 默认允许
     */
    @Column(name = "C_IS_MOBILE_ACCESS")
    private Boolean isMobileAccess=true;

    /**
     * 排序号
     */
    @Column(name = "C_SORT_NO")
    private Integer sortNo;

    /**
     * 失效时间
     */
    @Column(name = "C_AEAD_TIME")
    private Date aeadTime;

    /**
     * 固定电话
     */
    @Column(name = "C_PHONE")
    private String phone;

    /**
     * 地址
     */
    @Column(name = "C_ADDRESS")
    private String address;

    /**
     * 用户头像
     */
    @Column(name = "C_HEAD_IMG")
    private String headImg;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(Integer passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getLastLoginFailed() {
        return lastLoginFailed;
    }

    public void setLastLoginFailed(Date lastLoginFailed) {
        this.lastLoginFailed = lastLoginFailed;
    }

    public String getLastLoginClientIp() {
        return lastLoginClientIp;
    }

    public void setLastLoginClientIp(String lastLoginClientIp) {
        this.lastLoginClientIp = lastLoginClientIp;
    }

    public Boolean getMobileAccess() {
        return isMobileAccess;
    }

    public void setMobileAccess(Boolean mobileAccess) {
        isMobileAccess = mobileAccess;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Date getAeadTime() {
        return aeadTime;
    }

    public void setAeadTime(Date aeadTime) {
        this.aeadTime = aeadTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
