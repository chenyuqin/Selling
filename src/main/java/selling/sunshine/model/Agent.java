package selling.sunshine.model;

import org.springframework.util.StringUtils;

/**
 * Created by sunshine on 4/7/16.
 */
public class Agent extends Entity {
    private String agentId;
    private String name;
    private String gender;
    private String phone;
    private String address;
    private String password;
    private String wechat;
    private boolean granted;
    private double coffer;

    public Agent() {
        super();
        granted = false;
    }

    public Agent(String name, String gender, String phone, String address, String password) {
        this();
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    public Agent(String name, String gender, String phone, String address, String password, String wechat) {
        this(name, gender, phone, address, password);
        if (!StringUtils.isEmpty(wechat)) {
            this.wechat = wechat;
        }
    }
   
    public Agent(String agentId, String name, String gender, String phone,
			String address, String password, String wechat, boolean granted,
			double coffer) {
		this(name, gender, phone, address, password, wechat);
		this.coffer = coffer;
	}

	public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

	public double getCoffer() {
		return coffer;
	}

	public void setCoffer(double coffer) {
		this.coffer = coffer;
	}
    
    
}
