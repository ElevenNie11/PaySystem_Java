package modular;
//3. 子类具体实现：微信支付
public class WechatPay extends Payment{
    private String phoneNumber; //微信专有属性
    //构造方法：初始化对象
    WechatPay(String ownerName, String phoneNumber, double balance, String password){
        super(ownerName, balance, password);
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getType(){
        return "微信支付 WechatPay";
    }

    @Override
    public void pay(double amount, String inputPassword){
        checkPassword(inputPassword);

        if(!ifEnough(amount)){
            System.out.println("[支付失败]: 您的微信余额不足...");
            return ;
        }
        setBalance(amount);
        System.out.println("[支付成功]: 您已支付 " + amount + " 元");
        System.out.print("您的微信账户: " + phoneNumber + " 余额还剩 " + getBalance() +" 元");
    }
}